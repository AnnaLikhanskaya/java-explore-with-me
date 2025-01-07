package ru.practicum.admin.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {

    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategoryById(Long catId, CategoryDto updateCategory);

    List<EventFullDto> searchEventByCondition(List<Long> usersIds,
                                              List<String> eventsStates,
                                              List<Long> categoriesIds,
                                              LocalDateTime startDate,
                                              LocalDateTime endDate,
                                              Integer from,
                                              Integer size);


    EventFullDto editingEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<User> getUsersInformationByCondition(List<Long> usersIds, Integer from, Integer size);

    User addNewUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    CompilationDto addNewCompilations(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation);

    List<CommentDto> getUserCommentaries(Long userId, Integer from, Integer size);

    void deleteUserComment(Long commentId);
}