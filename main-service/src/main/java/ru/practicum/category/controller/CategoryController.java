package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос GET /categories, from={}, size={}", from, size);
        List<CategoryDto> categoryDtoList = categoryService.getCategories(from, size);
        log.info("Запрос GET /categories, from={}, size={}\nВозвращаемые данные: {}", from, size, categoryDtoList);
        return categoryDtoList;
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoriesById(@PathVariable(name = "catId") Long catId) {
        log.info("Запрос GET /categories/{}", catId);
        CategoryDto categoryDto = categoryService.getCategoriesById(catId);
        log.info("Запрос GET /categories/{}\nВозвращаемые данные: {}", catId, categoryDto);
        return categoryDto;
    }


}
