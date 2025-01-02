package ru.practicum.user.service;

import ru.practicum.event.model.EventFullDto;
import ru.practicum.event.model.EventShortDto;
import ru.practicum.event.model.NewEventDto;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;
import ru.practicum.request.model.ParticipationRequestDto;

import java.util.List;

public interface UserService {

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto addUserEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsOnUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsOnUserEvent(Long userId,
                                                             Long eventId,
                                                             EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getUserEventRequests(Long userId);

    ParticipationRequestDto addUserRequestOnEvent(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequestOnEvent(Long userId, Long requestId);

}