package ru.yandex.practicum.filmorate.exceptions;

public class FilmControllerException extends Exception {
    private final String message;

    public FilmControllerException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
