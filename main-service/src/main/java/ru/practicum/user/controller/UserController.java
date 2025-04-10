package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.ParticipationRequest;
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
    public List<ParticipationRequest> getRequestsOnUserEvent(@PathVariable(name = "userId") Long userId,
                                                             @PathVariable(name = "eventId") Long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        List<ParticipationRequest> participationRequestDtoList = userService.getRequestsOnUserEvent(userId, eventId);
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
    public List<ParticipationRequest> getUserEventRequests(@PathVariable(name = "userId") Long userId) {
        log.info("GET /users/{}/requests", userId);
        List<ParticipationRequest> participationRequestsList = userService.getUserEventRequests(userId);
        log.info("GET /users/{}/requests\n return: {}", userId, participationRequestsList);
        return participationRequestsList;
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequest addUserRequestOnEvent(@PathVariable(name = "userId") Long userId,
                                                      @RequestParam(name = "eventId") Long eventId) {
        log.info("GET /users/{}/requests, eventId={}", userId, eventId);
        ParticipationRequest participationRequestDto = userService.addUserRequestOnEvent(userId, eventId);
        log.info("GET /users/{}/requests, eventId={}\n return: {}", userId, eventId, participationRequestDto);
        return participationRequestDto;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequest cancelUserRequestOnEvent(@PathVariable(name = "userId") Long userId,
                                                         @PathVariable(name = "requestId") Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        ParticipationRequest request = userService.cancelUserRequestOnEvent(userId, requestId);
        log.info("PATCH /users/{}/requests/{}/cancel\n return: {}", userId, requestId, request);
        return request;
    }

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addUserComment(@PathVariable(name = "userId") Long userId,
                                     @PathVariable(name = "eventId") Long eventId,
                                     @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST /users/{}/events/{}/comments, body: {}", userId, eventId, newCommentDto);
        CommentDto commentDto = userService.addUserComment(userId, eventId, newCommentDto);
        log.info("POST /users/{}/events/{}/comments, body: {}\n return: {}", userId, eventId, newCommentDto, commentDto);
        return commentDto;
    }

    @PatchMapping("/{userId}/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateUserComment(@PathVariable(name = "userId") Long userId,
                                        @PathVariable(name = "eventId") Long eventId,
                                        @PathVariable(name = "commentId") Long commentId,
                                        @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("PATCH /users/{}/events/{}/comments/{}, body: {}", userId, eventId, commentId, updateCommentDto);
        CommentDto commentDto = userService.updateUserComment(userId, eventId, commentId, updateCommentDto);
        log.info("PATCH /users/{}/events/{}/comments/{}, body: {}\n return: {}",
                userId, eventId, commentId, updateCommentDto, commentDto);
        return commentDto;
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserComment(@PathVariable(name = "userId") Long userId,
                                  @PathVariable(name = "commentId") Long commentId) {
        log.info("DELETE /users/{}/comments/{}", userId, commentId);
        userService.deleteUserComment(userId, commentId);
        log.info("DELETE /users/{}/comments/{} success", userId, commentId);
    }

}