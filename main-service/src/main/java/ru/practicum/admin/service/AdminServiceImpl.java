package ru.practicum.admin.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.*;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.repository.CompilationsEventsRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.*;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Locations;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CompilationsEventsRepository compilationsEventsRepository;

    private final CompilationRepository compilationRepository;

    private final LocationRepository locationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        try {
            Category category = CategoryMapper.fromNewCategoryDtoToCategory(newCategoryDto);
            category = categoryRepository.save(category);
            log.info("Новая категория успешно добавлена: {}", category.getName());
            return CategoryMapper.fromCategoryToCategoryDto(category);
        } catch (DataAccessException e) {
            log.error("Ошибка при добавлении новой категории: {}", e.getMessage());
            throw new ConflictException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long catId) {
        idValidation(catId, "catId");
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id=" + catId + " не найдена"));

        List<EventShortDto> eventShortDto = eventRepository.findFirstEventByCategoryId(catId,
                PageRequest.of(0, 1));
        if (!eventShortDto.isEmpty()) {
            log.warn("Категория с id={} не пуста и не может быть удалена", catId);
            throw new ConditionsNotMetException("Категория не пуста");
        }

        categoryRepository.deleteById(catId);
        log.info("Категория с id={} успешно удалена", catId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategoryById(Long catId, CategoryDto updateCategory) {
        idValidation(catId, "catId");
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id= " + catId + " не найдена"));
        category.setName(updateCategory.getName());
        try {
            category = categoryRepository.save(category);
            log.info("Категория с id={} успешно обновлена", catId);
            return CategoryMapper.fromCategoryToCategoryDto(category);
        } catch (DataAccessException e) {
            log.error("Ошибка при обновлении категории с id={}: {}", catId, e.getMessage());
            throw new ConflictException(e.getMessage());
        }
    }

    @Transactional
    public List<EventFullDto> searchEventByCondition(List<Long> usersIds,
                                                     List<String> eventsStates,
                                                     List<Long> categoriesIds,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate,
                                                     Integer from,
                                                     Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (usersIds != null && !usersIds.isEmpty()) {
            predicates.add(root.get("initiator").get("id").in(usersIds));
        }
        if (eventsStates != null && !eventsStates.isEmpty()) {
            predicates.add(root.get("state").in(eventsStates));
        }
        if (categoriesIds != null && !categoriesIds.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categoriesIds));
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), endDate));
        }

        query.select(root).where(predicates.toArray(new Predicate[0]));

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        return events.stream()
                .map(EventMapper::fromEventToEventFullDto)
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto editingEventById(Long eventId, UpdateEventAdminRequest updateEvent) {
        idValidation(eventId, "eventId");
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.PUBLISH_EVENT.name())) {
                if (!oldEvent.getState().equals(EventState.PENDING.name())) {
                    log.warn("Невозможно опубликовать событие с id={}, так как оно не в состоянии PENDING", eventId);
                    throw new ConflictException("Невозможно опубликовать событие, так как оно не в состоянии PENDING: "
                            + oldEvent.getState());
                } else {
                    oldEvent.setState(EventState.PUBLISHED.name());
                    oldEvent.setPublishedOn(LocalDateTime.now());
                    log.info("Событие с id={} успешно опубликовано", eventId);
                }
            }
            if (updateEvent.getStateAction().equals((StateAction.REJECT_EVENT.name()))) {
                if (oldEvent.getState().equals(EventState.PUBLISHED.name())) {
                    log.warn("Невозможно отклонить событие с id={}, так как оно уже опубликовано", eventId);
                    throw new ConflictException("Невозможно отклонить событие, так как оно уже опубликовано: "
                            + oldEvent.getState());
                } else {
                    oldEvent.setState(EventState.CANCELED.name());
                    log.info("Событие с id={} успешно отклонено", eventId);
                }
            }
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now())) {
                log.warn("Дата события не может быть раньше текущей даты");
                throw new BadRequestException("Дата события должна быть позже текущей даты");
            }
            if (oldEvent.getState().equals(EventState.PUBLISHED.name())
                    && oldEvent.getPublishedOn().plusHours(1).isAfter(updateEvent.getEventDate())) {
                log.warn("Дата начала измененного события должна быть не раньше чем через час от даты публикации");
                throw new ConflictException("Дата начала измененного события должна быть не раньше чем через час от даты публикации");
            }
            oldEvent.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                    new ForbiddenException("Категория с id= " + updateEvent.getCategory() + " не найдена"));
            oldEvent.setCategory(category);
        }
        if (updateEvent.getDescription() != null) {
            oldEvent.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            Locations locations = locationRepository.save(LocationMapper.fromLocationToLocationDto(updateEvent.getLocation()));
            oldEvent.setLocation(locations);
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            oldEvent.setTitle(updateEvent.getTitle());
        }
        return EventMapper.fromEventToEventFullDto(eventRepository.save(oldEvent));
    }

    @Transactional
    @Override
    public List<User> getUsersInformationByCondition(List<Long> usersIds, Integer from, Integer size) {
        if (usersIds != null) {
            for (Long userId : usersIds) {
                idValidation(userId, "userId");
            }
        }
        if (from < 0) {
            log.warn("Параметр запроса 'from' должен быть больше 0, текущее значение from={}", from);
            throw new BadRequestException("Параметр запроса 'from' должен быть больше 0, текущее значение from=" + from);
        }
        if (size < 0) {
            log.warn("Параметр запроса 'size' должен быть больше 0, текущее значение size={}", size);
            throw new BadRequestException("Параметр запроса 'size' должен быть больше 0, текущее значение size=" + size);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return userRepository.findAllUsersByCondition(usersIds, pageable);
    }

    @Transactional
    @Override
    public User addNewUser(NewUserRequest newUserRequest) {
        User newUser = UserMapper.fromNewUserRequestToUserDto(newUserRequest);
        try {
            log.info("Добавление нового пользователя: {}", newUser.getName());
            return userRepository.save(newUser);
        } catch (DataAccessException e) {
            log.error("Ошибка при добавлении нового пользователя: {}", e.getMessage());
            throw new ConflictException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        idValidation(userId, "userId");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id= "
                + userId + " не найден"));
        userRepository.deleteById(userId);
        log.info("Пользователь с id={} успешно удален", userId);
    }

    @Transactional
    @Override
    public CompilationDto addNewCompilations(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        if (newCompilationDto.getEvents() == null) {
            newCompilationDto.setEvents(new ArrayList<>());
        }
        Compilation compilation = compilationRepository.save(CompilationMapper.fromNewCompilationDtoToCompilation(newCompilationDto));
        CompilationDto resultCompilationDto = CompilationMapper.fromCompilationToCompilationDto(compilation);
        if (newCompilationDto.getEvents().isEmpty()) {
            resultCompilationDto.setEvents(new ArrayList<>());
            log.info("Новая подборка успешно добавлена без событий");
            return resultCompilationDto;
        }

        List<Long> eventIdsList = newCompilationDto.getEvents();
        List<CompilationsEvents> compilationsEventsList = eventIdsList.stream()
                .map(eventId -> new CompilationsEvents(resultCompilationDto.getId(), eventId))
                .toList();
        compilationsEventsRepository.saveAll(compilationsEventsList);

        List<EventShortDto> eventShortDtoList = eventRepository.findAllEventsByIds(eventIdsList);
        resultCompilationDto.setEvents(eventShortDtoList);

        log.info("Новая подборка успешно добавлена с событиями");
        return resultCompilationDto;
    }

    @Transactional
    @Override
    public void deleteCompilationById(Long compId) {
        idValidation(compId, "compId");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с id=" + compId + " не найдена"));
        compilationRepository.deleteById(compId);
        log.info("Подборка с id={} успешно удалена", compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation) {
        idValidation(compId, "compId");
        Compilation oldCompilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с id=" + compId + " не найдена"));
        if (updateCompilation.getPinned() != null) {
            oldCompilation.setPinned(updateCompilation.getPinned());
        }
        if (updateCompilation.getTitle() != null) {
            if (updateCompilation.getTitle().length() < 1 && updateCompilation.getTitle().length() > 50) {
                throw new ForbiddenException("Длина поля 'title' должна быть от 1 до 50 символов, текущая длина="
                        + updateCompilation.getTitle().length());
            }
            oldCompilation.setTitle(updateCompilation.getTitle());
        }
        compilationRepository.save(oldCompilation);
        CompilationDto resultDto = CompilationMapper.fromCompilationToCompilationDto(oldCompilation);
        resultDto.setEvents(new ArrayList<>());

        if (updateCompilation.getEvents() != null) {
            List<CompilationsEvents> oldCompilationsEventsList = compilationsEventsRepository.findAllCompilationsEventsByCompilationId(compId);
            List<Long> oldEventsIds = oldCompilationsEventsList.stream().map(CompilationsEvents::getEventId).toList();
            List<Long> newEventsIds = new ArrayList<>(updateCompilation.getEvents());
            List<Long> eventsIdsForDelete = new ArrayList<>();
            List<Long> eventsIdsForAdd = new ArrayList<>();

            if (newEventsIds.isEmpty() && oldEventsIds.isEmpty()) {
                log.info("Список событий не изменился");
                return resultDto;

            } else if (!newEventsIds.isEmpty() && oldEventsIds.isEmpty()) {
                eventsIdsForAdd.addAll(newEventsIds);
            } else if (newEventsIds.isEmpty() && !oldEventsIds.isEmpty()) {
                eventsIdsForDelete.addAll(oldEventsIds);
            } else {
                eventsIdsForAdd = newEventsIds.stream()
                        .filter(id -> !oldEventsIds.contains(id))
                        .collect(Collectors.toList());

                eventsIdsForDelete = oldEventsIds.stream()
                        .filter(id -> !newEventsIds.contains(id))
                        .collect(Collectors.toList());
            }

            if (!eventsIdsForDelete.isEmpty()) {
                compilationsEventsRepository.deleteAllByEventsIds(eventsIdsForDelete);
                log.info("Удалены события с id={}", eventsIdsForDelete);
            }
            if (!eventsIdsForAdd.isEmpty()) {
                List<CompilationsEvents> newCompilationsEventsList = eventsIdsForAdd.stream()
                        .map(eventId -> new CompilationsEvents(compId, eventId))
                        .toList();
                compilationsEventsRepository.saveAll(newCompilationsEventsList);
                log.info("Добавлены новые события с id={}", eventsIdsForAdd);

            }
            List<EventShortDto> eventShortDtoList = eventRepository.findAllEventsByIds(newEventsIds);
            resultDto.setEvents(eventShortDtoList);
        }

        log.info("Подборка с id={} успешно обновлена", compId);
        return resultDto;
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            log.warn("Поле {} должно быть больше 0, текущее значение {}={}", fieldName, fieldName, id);
            throw new BadRequestException("Поле " + fieldName + " должно быть больше 0, текущее значение "
                    + fieldName + "=" + id);
        }
    }
}
