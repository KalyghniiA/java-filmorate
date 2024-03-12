package ru.yandex.practicum.filmorate.exceptions;

public class EmptyBodyException extends Exception {
    private final String message;

    public EmptyBodyException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
