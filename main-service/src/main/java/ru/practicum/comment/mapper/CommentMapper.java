package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment fromNewCommentDtoToComment(NewCommentDto newCommentDto, Event event, User user) {
        return Comment.builder()
                .id(null)
                .event(event)
                .text(newCommentDto.getText())
                .commentator(user)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static CommentDto fromCommentToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .event(EventMapper.fromEventToEventShortDto(comment.getEvent()))
                .text(comment.getText())
                .commentator(UserMapper.fromUserDtoToUserShortDto(comment.getCommentator()))
                .createdOn(comment.getCreatedOn())
                .build();
    }
}