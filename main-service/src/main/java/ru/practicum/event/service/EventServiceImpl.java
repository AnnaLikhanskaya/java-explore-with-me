package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatsClient;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final StatsClient statsClient;

    private final EventRepository eventRepository;

    @Transactional
    @Override
    public List<EventShortDto> getEventsByCondition(String text,
                                                    List<Long> categoriesIds,
                                                    Boolean isPaid,
                                                    String startDate,
                                                    String endDate,
                                                    Boolean isAvailable,
                                                    String sort,
                                                    Integer from,
                                                    Integer size,
                                                    HttpServletRequest request) {
        if (categoriesIds != null) {
            for (Long categoryId : categoriesIds) {
                idValidation(categoryId, "categoryId");
            }
        }

        if (from < 0) {
            throw new BadRequestException("Параметр запроса 'from' должен быть больше 0, текущее значение from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Параметр запроса 'size' должен быть больше 0, текущее значение size=" + size);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        if (text == null) {
            text = "";
        }
        if (categoriesIds == null) {
            categoriesIds = new ArrayList<>();
        }
        String sortField = "e.id";
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                sortField = "e.eventDate";
            } else if (sort.equals("VIEWS")) {
                sortField = "e.views";
            } else {
                throw new BadRequestException("Некорректное поле сортировки");
            }
        }
        List<Event> events;
        if (startDate == null && endDate == null) {
            events = eventRepository.findAllEventsByFilterWithoutTime("%" + text.toLowerCase() + "%",
                    categoriesIds,
                    isPaid,
                    pageable,
                    EventState.PUBLISHED.name(),
                    sort);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime parsedStartDate = LocalDateTime.parse(startDate, formatter);
            LocalDateTime parsedEndDate = LocalDateTime.parse(endDate, formatter);
            if (parsedEndDate.isBefore(parsedStartDate)) {
                throw new BadRequestException("Дата окончания не может быть раньше даты начала");
            }

            events = eventRepository.findAllEventsByFilter("%" + text.toLowerCase() + "%",
                    categoriesIds,
                    isPaid,
                    parsedStartDate,
                    parsedEndDate,
                    pageable,
                    EventState.PUBLISHED.name(),
                    sort);
        }

        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.postHit(endpointHitDto);
        return events.stream().map(EventMapper::fromEventToEventShortDto).toList();
    }

    @Transactional
    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        idValidation(eventId, "eventId");
        Event resultEvent = eventRepository.findEventByEventId(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " не найдено"));
        if (!resultEvent.getState().equals(EventState.PUBLISHED.name())) {
            throw new NotFoundException("Событие недоступно");
        }
        resultEvent.setViews(resultEvent.getViews() + 1);
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.postHit(endpointHitDto);
        List<ViewStatsDto> viewStatsDtoList = statsClient.getStats(resultEvent.getCreatedOn(),
                resultEvent.getEventDate(),
                List.of(request.getRequestURI()),
                true);
        resultEvent.setViews(viewStatsDtoList.size());
        return EventMapper.fromEventToEventFullDto(eventRepository.save(resultEvent));
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Поле " + fieldName + " должно быть больше 0, текущее значение "
                    + fieldName + "=" + id);
        }
    }

}