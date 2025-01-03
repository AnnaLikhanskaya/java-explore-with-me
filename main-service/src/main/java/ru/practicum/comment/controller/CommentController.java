package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllEventCommentaries(@PathVariable(name = "eventId") Long eventId,
                                                    @RequestParam(defaultValue = "0", name = "from") Integer from,
                                                    @RequestParam(defaultValue = "10", name = "size") Integer size) {
        log.info("GET /comments/events/{}", eventId);
        List<CommentDto> commentDtoList = commentService.getAllEventCommentaries(eventId, from, size);
        log.info("GET /comments/events/{}\n return: {}", eventId, commentDtoList);
        return commentDtoList;
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable(name = "commentId") Long commentId) {
        log.info("GET /comments/{}", commentId);
        CommentDto commentDto = commentService.getCommentById(commentId);
        log.info("GET /comments/{}\n return: {}", commentId, commentDto);
        return commentDto;
    }

}