package ru.yandex.practicum.filmorate.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.EmptyBodyException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserException;
import ru.yandex.practicum.filmorate.exceptions.UserFriendException;
import ru.yandex.practicum.filmorate.model.errorResponce.ErrorResponse;

@RestControllerAdvice(basePackages = "ru.yandex.practicum.filmorate.controller.user")
public class UserErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(NotFoundUserException e) {
        return new ErrorResponse("user", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationException(MethodArgumentNotValidException e) {
        return new ErrorResponse("user", e.getMessage());
    }

    @ExceptionHandler({EmptyBodyException.class, UserFriendException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerInternalException(Exception e) {
        return new ErrorResponse("user", e.getMessage());
    }
}
