package ru.yandex.practicum.filmorate.exceptions;

public class UserControllerException extends Exception {
    private String message;

    public UserControllerException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
