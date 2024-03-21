package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends Exception {
    private final String message;

    public ValidationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
