package ru.practicum.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.AdminService;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addNewCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("POST /admin/categories body: {}", newCategoryDto);
        CategoryDto categoryDto = adminService.addNewCategory(newCategoryDto);
        log.info("POST /admin/categories body: {} \n return: {}", newCategoryDto, categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable(name = "catId") Long catId) {
        log.info("DELETE /admin/categories/{}", catId);
        adminService.deleteCategoryById(catId);
        log.info("DELETE /admin/categories/{} DONE", catId);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategoryById(@PathVariable(name = "catId") Long catId,
                                          @Valid @RequestBody CategoryDto updateCategory) {
        log.info("PATCH /admin/categories/{}, body: {}", catId, updateCategory);
        CategoryDto categoryDto = adminService.updateCategoryById(catId, updateCategory);
        log.info("PATCH /admin/categories/{}, body: {}\n return : {}", catId, updateCategory, categoryDto);
        return categoryDto;
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEventByCondition(
            @RequestParam(required = false, name = "users") List<Long> usersIds,
            @RequestParam(required = false, name = "states") List<String> eventsStates,
            @RequestParam(required = false, name = "categories") List<Long> categoriesIds,
            @RequestParam(required = false, name = "rangeStart") String startDate,
            @RequestParam(required = false, name = "rangeEnd") String endDate,
            @RequestParam(defaultValue = "0", name = "from") Integer from,
            @RequestParam(defaultValue = "10", name = "size") Integer size) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = startDate != null ? LocalDateTime.parse(startDate, formatter) : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.parse(endDate, formatter) : null;

        log.info("GET admin/events, users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                usersIds, eventsStates, categoriesIds, startDate, endDate, from, size);

        List<EventFullDto> eventShortDtoList = adminService.searchEventByCondition(usersIds,
                eventsStates,
                categoriesIds,
                startDateTime,
                endDateTime,
                from,
                size);
        log.info("GET admin/events, users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size{}\n" +
                "return: {}", usersIds, eventsStates, categoriesIds, startDate, endDate, from, size, eventShortDtoList);
        return eventShortDtoList;
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto editingEventById(@PathVariable(name = "eventId") Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("PATCH admin/events/{}, body:{}", eventId, updateEventAdminRequest);
        EventFullDto eventFullDto = adminService.editingEventById(eventId, updateEventAdminRequest);
        log.info("PATCH admin/events/{}, body:{}\n return: {}", eventId, updateEventAdminRequest, eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsersInformationByCondition(@RequestParam(required = false, name = "ids") List<Long> usersIds,
                                                     @RequestParam(defaultValue = "0", name = "from") Integer from,
                                                     @RequestParam(defaultValue = "10", name = "size") Integer size) {
        log.info("GET /admin/users, ids={}. from={}, size={}", usersIds, from, size);
        List<User> userList = adminService.getUsersInformationByCondition(usersIds, from, size);
        log.info("GET /admin/users, ids={}. from={}, size={}\n return: {}", usersIds, from, size, userList);
        return userList;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("POST /admin/users, body: {}", newUserRequest);
        User user = adminService.addNewUser(newUserRequest);
        log.info("POST /admin/users, body: {}\n return: {}", newUserRequest, user);
        return user;
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        log.info("DELETE /admin/users/{}", userId);
        adminService.deleteUser(userId);
        log.info("DELETE /admin/users/{}, success", userId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNewCompilations(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("POST /admin/compilations, body: {}", newCompilationDto);
        CompilationDto compilationDto = adminService.addNewCompilations(newCompilationDto);
        log.info("POST /admin/compilations, body: {}\n return: {}", newCompilationDto, compilationDto);
        return compilationDto;
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable(name = "compId") Long compId) {
        log.info("DELETE /admin/compilations/{}", compId);
        adminService.deleteCompilationById(compId);
        log.info("DELETE /admin/compilations/{}, success", compId);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable(name = "compId") Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilation) {
        log.info("PATCH /admin/compilations/{}, body: {}", compId, updateCompilation);
        CompilationDto compilationDto = adminService.updateCompilation(compId, updateCompilation);
        log.info("PATCH /admin/compilations/{}, body: {}\n return: {}", compId, updateCompilation, compilationDto);
        return compilationDto;
    }

    @GetMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getUserCommentaries(@PathVariable(name = "userId") Long userId,
                                                @RequestParam(defaultValue = "0", name = "from") Integer from,
                                                @RequestParam(defaultValue = "10", name = "size") Integer size) {
        log.info("GET /admin/users/{}/comments", userId);
        List<CommentDto> commentDtoList = adminService.getUserCommentaries(userId, from, size);
        log.info("GET /admin/users/{}/comments\n return: {}", userId, commentDtoList);
        return commentDtoList;
    }

    @DeleteMapping("/users/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserComment(@PathVariable(name = "commentId") Long commentId) {
        log.info("DELETE /admin/users/comments/{}", commentId);
        adminService.deleteUserComment(commentId);
        log.info("DELETE /admin/users/comments/{} success", commentId);
    }


}