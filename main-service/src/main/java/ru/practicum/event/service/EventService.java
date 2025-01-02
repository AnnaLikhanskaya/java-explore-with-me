package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.model.EventFullDto;
import ru.practicum.event.model.EventShortDto;

import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByCondition(String text,
                                             List<Long> categoriesIds,
                                             Boolean isPaid,
                                             String startDate,
                                             String endDate,
                                             Boolean isAvailable,
                                             String sort,
                                             Integer from,
                                             Integer size,
                                             HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}