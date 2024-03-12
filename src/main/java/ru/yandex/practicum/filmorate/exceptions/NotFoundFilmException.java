package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundFilmException extends Exception {
    private String message;

    public NotFoundFilmException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
