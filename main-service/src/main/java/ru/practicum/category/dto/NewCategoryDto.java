package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    @Size(min = 1, max = 50, message = "Имя должно содержать от 1 до 50 символов")
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
}
