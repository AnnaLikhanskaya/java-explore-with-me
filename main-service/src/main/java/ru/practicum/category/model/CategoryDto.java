package ru.practicum.category.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    @Size(min = 1, max = 50, message = "Имя должно содержать от 1 до 50 символов")
    private String name;
}