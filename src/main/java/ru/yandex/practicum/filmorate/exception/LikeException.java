package ru.yandex.practicum.filmorate.exception;

public class LikeException extends RuntimeException {
    private final String message;

    public LikeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
