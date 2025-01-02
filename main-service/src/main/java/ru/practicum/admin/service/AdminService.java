package ru.practicum.admin.service;

import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.model.NewCategoryDto;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.NewCompilationDto;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.event.model.EventFullDto;
import ru.practicum.event.model.UpdateEventAdminRequest;
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

}