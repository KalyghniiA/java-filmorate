package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends Exception {
    private String message;

    public ValidationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
