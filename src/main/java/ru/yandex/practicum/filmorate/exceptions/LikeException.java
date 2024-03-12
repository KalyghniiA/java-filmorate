package ru.yandex.practicum.filmorate.exceptions;

public class LikeException extends Exception {
    private final String message;

    public LikeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
