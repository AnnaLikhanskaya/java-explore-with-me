package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.LocationDto;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;
import ru.practicum.request.model.ParticipationRequestDto;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final LocationRepository locationRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        idValidation(userId, "userId");
        if (from < 0) {
            throw new BadRequestException("Параметр запроса 'from' должен быть больше 0, текущее значение from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Параметр запроса 'size' должен быть больше 0, текущее значение size=" + size);
        }
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));


        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllEventsByInitiatorId(userId, pageable);
    }

    @Override
    public EventFullDto addUserEvent(Long userId, NewEventDto newEventDto) {
        idValidation(userId, "userId");
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Дата события должна быть не раньше чем через два часа от текущего времени, " +
                    "текущее значение eventDate=" + newEventDto.getEventDate());
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("Категория с id=" + newEventDto.getCategory() + " не найдена"));
        LocationDto locationDto = locationRepository.save(LocationMapper.fromLocationToLocationDto(newEventDto.getLocation()));
        Event event = EventMapper.fromNewEventDtoToEvent(newEventDto,
                category,
                user,
                locationDto);


        return EventMapper.fromEventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id="
                + userId + " не найден"));
        return EventMapper.fromEventToEventFullDto(eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " не найдено")));
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Дата события должна быть в будущем");
            }
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id="
                + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
        if (event.getState().equals(EventState.PUBLISHED.name())) {
            throw new ConflictException("Событие уже опубликовано и не может быть изменено пользователем");
        }

        return EventMapper.fromEventToEventFullDto(eventRepository.save(updateEvent(event, updateEventUserRequest)));
    }

    private Event updateEvent(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Дата события должна быть не раньше чем через два часа от текущего времени, " +
                        "текущее значение eventDate="
                        + updateEventUserRequest.getEventDate());
            }
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            if (updateEventUserRequest.getAnnotation().length() < 20
                    || updateEventUserRequest.getAnnotation().length() > 2000) {
                throw new ForbiddenException("Длина аннотации должна быть от 20 до 2000 символов, " +
                        "текущая длина=" + updateEventUserRequest.getAnnotation().length());
            }
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            if (updateEventUserRequest.getCategory() < 0) {
                throw new ForbiddenException("Идентификатор категории должен быть больше или равен 0, " +
                        "текущее значение category=" + updateEventUserRequest.getCategory());
            }
            Category category = categoryRepository.findById(Long.valueOf(updateEventUserRequest.getCategory())).orElseThrow(() ->
                    new NotFoundException("Категория с id=" + updateEventUserRequest.getCategory() + " не найдена"));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            if (updateEventUserRequest.getDescription().length() < 20
                    || updateEventUserRequest.getDescription().length() > 7000) {
                throw new ForbiddenException("Длина описания должна быть от 20 до 7000 символов, " +
                        "текущая длина=" + updateEventUserRequest.getDescription().length());
            }
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getLocation() != null) {
            LocationDto locationDto = locationRepository.save(LocationMapper.fromLocationToLocationDto(updateEventUserRequest.getLocation()));
            event.setLocation(locationDto);
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW.name())) {
                event.setState(EventState.PENDING.name());
            } else if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW.name())) {
                event.setState(EventState.CANCELED.name());
            } else {
                throw new ForbiddenException("Действие состояния должно быть SEND_TO_REVIEW или " +
                        "CANCEL_REVIEW, текущее значение stateAction=" + updateEventUserRequest.getStateAction());
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            if (updateEventUserRequest.getTitle().length() < 3 || updateEventUserRequest.getTitle().length() > 120) {
                throw new ForbiddenException("Длина названия должна быть от 3 до 120 символов, " +
                        "текущая длина=" + updateEventUserRequest.getTitle().length());
            }
            event.setTitle(updateEventUserRequest.getTitle());
        }

        return event;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnUserEvent(Long userId, Long eventId) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Только инициатор события может получить запросы");
        }

        return requestRepository.findAllRequestsByEventId(eventId);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsOnUserEvent(Long userId,
                                                                    Long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Event event = eventRepository.findEventByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Только инициатор события может обновить запросы");
        }
        List<Long> requestsIds = updateRequest.getRequestIds();

        List<ParticipationRequestDto> requestDtoListBefore = requestRepository.findAllById(requestsIds);
        int confirmedRequests = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();

        if (participantLimit != 0 && ((participantLimit - confirmedRequests) == 0)) {
            throw new ConflictException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное " +
                    "событие");
        }
        int mayBeAccept;
        if (participantLimit == 0 || !event.getRequestModeration()) {
            mayBeAccept = Integer.MAX_VALUE;
        } else {
            mayBeAccept = participantLimit - confirmedRequests;
        }

        List<ParticipationRequestDto> resultConfirmedRequestsList = new ArrayList<>();
        List<ParticipationRequestDto> resultRejectedRequestsList = new ArrayList<>();

        if (updateRequest.getStatus().equals(RequestStatus.REJECTED.name())) {
            for (ParticipationRequestDto requestDto : requestDtoListBefore) {
                if (!requestDto.getStatus().equals(RequestStatus.PENDING.name())) {
                    throw new BadRequestException("Все заявки должны быть в статусе PENDING");
                } else {
                    requestDto.setStatus(RequestStatus.REJECTED.name());
                    resultRejectedRequestsList.add(requestDto);
                }
            }
        } else {
            for (ParticipationRequestDto requestDto : requestDtoListBefore) {
                if (mayBeAccept == 0) {
                    requestDto.setStatus(RequestStatus.REJECTED.name());
                    resultRejectedRequestsList.add(requestDto);
                } else {
                    if (!requestDto.getStatus().equals(RequestStatus.PENDING.name())) {
                        throw new BadRequestException("Все заявки должны быть в статусе PENDING");
                    } else {
                        requestDto.setStatus(RequestStatus.CONFIRMED.name());
                        resultConfirmedRequestsList.add(requestDto);
                        confirmedRequests++;
                        mayBeAccept--;
                    }
                }
            }
        }
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
        List<ParticipationRequestDto> updateRequests = new ArrayList<>(resultConfirmedRequestsList);
        updateRequests.addAll(resultRejectedRequestsList);
        requestRepository.saveAll(updateRequests);

        return EventRequestStatusUpdateResult.builder()
                .rejectedRequests(resultRejectedRequestsList)
                .confirmedRequests(resultConfirmedRequestsList)
                .build();
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(Long userId) {
        idValidation(userId, "userId");
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));


        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    public ParticipationRequestDto addUserRequestOnEvent(Long userId, Long eventId) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Запрос от этого пользователя на это событие уже существует");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор события не может создать запрос на свое событие");
        }
        if (!event.getState().equals(EventState.PUBLISHED.name())) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getParticipantLimit() != 0) {
            if (event.getParticipantLimit() - event.getConfirmedRequests() < 1) {
                throw new ConflictException("Лимит участников для события превышен");
            }
        }

        ParticipationRequestDto newRequest = ParticipationRequestDto.builder()
                .id(null)
                .created(LocalDateTime.now())
                .event(eventId)
                .requester(userId)
                .status(null)
                .build();
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            newRequest.setStatus(RequestStatus.PENDING.name());
        } else {
            newRequest.setStatus(RequestStatus.CONFIRMED.name());
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return requestRepository.save(newRequest);
    }

    @Override
    public ParticipationRequestDto cancelUserRequestOnEvent(Long userId, Long requestId) {
        idValidation(userId, "userId");
        idValidation(requestId, "requestId");

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));
        ParticipationRequestDto request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с id=" + requestId + " не найден"));
        request.setStatus(RequestStatus.CANCELED.name());

        return requestRepository.save(request);
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Поле " + fieldName + " должно быть больше 0, текущее значение "
                    + fieldName + "=" + id);
        }
    }

}