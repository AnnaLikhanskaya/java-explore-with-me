package ru.practicum.category.service;

import ru.practicum.category.model.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoriesById(Long catId);

}