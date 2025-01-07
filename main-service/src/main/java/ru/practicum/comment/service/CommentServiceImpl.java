package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getAllEventCommentaries(Long eventId, Integer from, Integer size) {
        idValidation(eventId, "eventId");
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с идентификатором= " + eventId + " не найдено"));
        if (from < 0) {
            throw new BadRequestException("Параметр запроса from должен быть больше 0, теперь from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Параметр запроса 'size' должен быть больше 0, теперь size=" + size);
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> commentList = commentRepository.findAllCommentariesByEventId(eventId, pageable);

        if (commentList.isEmpty()) {
            return new ArrayList<>();
        }
        return commentList.stream().map(CommentMapper::fromCommentToCommentDto).toList();
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        idValidation(commentId, "commentId");
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с идентификатором= " + commentId + " не найден"));
        return CommentMapper.fromCommentToCommentDto(comment);
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Поле " + fieldName + " должно быть больше 0\" " +
                    "сейчас " + fieldName + "=" + id);
        }
    }
}
