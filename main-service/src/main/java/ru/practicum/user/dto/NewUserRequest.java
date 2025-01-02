package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Не должно быть пустым")
    @Email(message = "Не является корректным email адресом")
    @Size(max = 254, min = 6, message = "Email должен содержать от 6 до 254 символов")
    private String email;

    @NotBlank(message = "Не должно быть пустым")
    @Size(max = 250, min = 2, message = "Имя должно содержать от 2 до 250 символов")
    private String name;
}