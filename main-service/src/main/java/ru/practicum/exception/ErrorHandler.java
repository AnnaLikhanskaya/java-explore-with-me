package ru.practicum.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingParam(MissingServletRequestParameterException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Недостаточно обязательных параметров запроса")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(final MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError ->
                        String.format("Поле: %s. Ошибка: %s. Значение: %s",
                                fieldError.getField(),
                                fieldError.getDefaultMessage(),
                                fieldError.getRejectedValue() != null ?
                                        fieldError.getRejectedValue() : "null"))
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Некорректный запрос")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("Объект не найден")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.CONFLICT.toString())
                .reason("Нарушение целостности данных")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbidden(final ForbiddenException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.FORBIDDEN.toString())
                .reason("Условия для выполнения операции не выполнены")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final BadRequestException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Некорректный запрос")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .reason("Что-то пошло не так")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler({ConditionsNotMetException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConditionsNotMetException(final Exception e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.CONFLICT.toString())
                .reason("Условия для выполнения операции не выполнены")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
