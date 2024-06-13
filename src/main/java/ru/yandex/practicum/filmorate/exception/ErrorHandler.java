package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice(basePackages = "ru.yandex.practicum.filmorate.controller")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({
            EmptyResultDataAccessException.class,
            NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(RuntimeException e) {
        log.error("Error", e);
        return new ErrorResponse("film", e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            DuplicateKeyException.class,
            ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationException(RuntimeException e) {
        log.error("Error", e);
        return new ErrorResponse("film", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerInternalException(Exception e) {
        log.error("Error", e);
        return new ErrorResponse("film", e.getMessage());
    }

}
