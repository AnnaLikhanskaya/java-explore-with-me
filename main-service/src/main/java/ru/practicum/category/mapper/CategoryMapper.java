package ru.practicum.category.mapper;

import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.model.NewCategoryDto;

public class CategoryMapper {

    public static CategoryDto fromCategoryToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category fromNewCategoryDtoToCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .id(null)
                .name(newCategoryDto.getName())
                .build();
    }
}