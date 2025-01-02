package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {

    @NotBlank(message = "Не должно быть пустым")
    @Size(max = 2000, min = 20, message = "Аннотация должна содержать от 20 до 2000 символов")
    private String annotation;

    @NotNull(message = "Не должно быть null")
    @Positive(message = "Должно быть положительным числом")
    private Long category;

    @NotBlank(message = "Не должно быть пустым")
    @Size(max = 7000, min = 20, message = "Описание должно содержать от 20 до 7000 символов")
    private String description;

    @NotNull(message = "Не должно быть null")
    @Future(message = "Дата должна быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Не должно быть null")
    private Location location;

    private Boolean paid;

    @PositiveOrZero(message = "Должно быть больше или равно 0")
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "Не должно быть пустым")
    @Size(max = 120, min = 3, message = "Название должно содержать от 3 до 120 символов")
    private String title;
}