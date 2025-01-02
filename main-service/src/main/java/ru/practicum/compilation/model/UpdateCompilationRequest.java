package ru.practicum.compilation.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {

    @UniqueElements
    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Название должно содержать от 1 до 50 символов")
    private String title;
}