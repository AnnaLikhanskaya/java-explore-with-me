package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllEventCommentaries(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);
}
