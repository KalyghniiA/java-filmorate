package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundUserException extends Exception {
    String message;

    public NotFoundUserException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
