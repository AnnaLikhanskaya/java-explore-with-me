package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.EventFullDto;
import ru.practicum.event.model.EventShortDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByCondition(@RequestParam(required = false, name = "text") String text,
                                                    @RequestParam(required = false, name = "categories") List<Long> categoriesIds,
                                                    @RequestParam(required = false, name = "paid") Boolean isPaid,
                                                    @RequestParam(required = false, name = "rangeStart") String startDate,
                                                    @RequestParam(required = false, name = "rangeEnd") String endDate,
                                                    @RequestParam(defaultValue = "false", name = "onlyAvailable") Boolean isAvailable,
                                                    @RequestParam(required = false, name = "sort") String sort,
                                                    @RequestParam(defaultValue = "0", name = "from") Integer from,
                                                    @RequestParam(defaultValue = "10", name = "size") Integer size,
                                                    HttpServletRequest request) {
        log.info("GET /events, text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, " +
                        "sort={}, from={}, size={}", text, categoriesIds, isPaid, startDate, endDate, isAvailable,
                sort, from, size);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());

        List<EventShortDto> eventShortDtoList = eventService.getEventsByCondition(text, categoriesIds, isPaid, startDate,
                endDate, isAvailable, sort, from, size, request);
        log.info("GET /events, text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, " +
                        "sort={}, from={}, size={}\n return: {}", text, categoriesIds, isPaid, startDate, endDate,
                isAvailable, sort, from, size, eventShortDtoList);
        return eventShortDtoList;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable(name = "id") Long eventId, HttpServletRequest request) {
        log.info("GET /events/{}", eventId);
        EventFullDto eventFullDto = eventService.getEventById(eventId, request);
        log.info("GET /events/{}\n return: {}", eventId, eventFullDto);
        return eventFullDto;
    }

}