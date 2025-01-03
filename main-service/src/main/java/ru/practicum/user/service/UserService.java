package ru.practicum.user.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;

public interface UserService {

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto addUserEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequest> getRequestsOnUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsOnUserEvent(Long userId,
                                                             Long eventId,
                                                             EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequest> getUserEventRequests(Long userId);

    ParticipationRequest addUserRequestOnEvent(Long userId, Long eventId);

    ParticipationRequest cancelUserRequestOnEvent(Long userId, Long requestId);

}