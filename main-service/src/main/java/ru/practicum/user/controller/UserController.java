package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.EventFullDto;
import ru.practicum.event.model.EventShortDto;
import ru.practicum.event.model.NewEventDto;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;
import ru.practicum.request.model.ParticipationRequestDto;
import ru.practicum.user.service.UserService;

import java.util.List;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable(name = "userId") Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /users/{}/events, from={}, size={}", userId, from, size);
        List<EventShortDto> userEvents = userService.getUserEvents(userId, from, size);
        log.info("GET /users/{}/events, from={}, size={}\n return: {}", userId, from, size, userEvents);
        return userEvents;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addUserEvent(@PathVariable(name = "userId") Long userId,
                                     @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST /users/{}/events, body: {}", userId, newEventDto);
        EventFullDto eventFullDto = userService.addUserEvent(userId, newEventDto);
        log.info("POST /users/{}/events, body: {}\n return: {}", userId, newEventDto, eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getUserEventById(@PathVariable(name = "userId") Long userId,
                                         @PathVariable(name = "eventId") Long eventId) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        EventFullDto eventFullDto = userService.getUserEventById(userId, eventId);
        log.info("GET /users/{}/events/{}\n return: {}", userId, eventId, eventFullDto);
        return eventFullDto;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateUserEvent(@PathVariable(name = "userId") Long userId,
                                        @PathVariable(name = "eventId") Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("PATCH /users/{}/events/{}, body: {}", userId, eventId, updateEventUserRequest);
        EventFullDto eventFullDto = userService.updateUserEvent(userId, eventId, updateEventUserRequest);
        log.info("PATCH /users/{}/events/{}, body: {},\n return: {}",
                userId, eventId, updateEventUserRequest, eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsOnUserEvent(@PathVariable(name = "userId") Long userId,
                                                                @PathVariable(name = "eventId") Long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        List<ParticipationRequestDto> participationRequestDtoList = userService.getRequestsOnUserEvent(userId, eventId);
        log.info("GET /users/{}/events/{}/requests, return: {}", userId, eventId, participationRequestDtoList);
        return participationRequestDtoList;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsOnUserEvent(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("PATCH /users/{}/events/{}/requests, body: {}", userId, eventId, updateRequest);
        EventRequestStatusUpdateResult result = userService.updateRequestsOnUserEvent(userId, eventId, updateRequest);
        log.info("PATCH /users/{}/events/{}/requests, body: {}\n return: {}", userId, eventId, updateRequest, result);
        return result;
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable(name = "userId") Long userId) {
        log.info("GET /users/{}/requests", userId);
        List<ParticipationRequestDto> participationRequestsList = userService.getUserEventRequests(userId);
        log.info("GET /users/{}/requests\n return: {}", userId, participationRequestsList);
        return participationRequestsList;
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addUserRequestOnEvent(@PathVariable(name = "userId") Long userId,
                                                         @RequestParam(name = "eventId") Long eventId) {
        log.info("GET /users/{}/requests, eventId={}", userId, eventId);
        ParticipationRequestDto participationRequestDto = userService.addUserRequestOnEvent(userId, eventId);
        log.info("GET /users/{}/requests, eventId={}\n return: {}", userId, eventId, participationRequestDto);
        return participationRequestDto;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelUserRequestOnEvent(@PathVariable(name = "userId") Long userId,
                                                            @PathVariable(name = "requestId") Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        ParticipationRequestDto request = userService.cancelUserRequestOnEvent(userId, requestId);
        log.info("PATCH /users/{}/requests/{}/cancel\n return: {}", userId, requestId, request);
        return request;
    }

}