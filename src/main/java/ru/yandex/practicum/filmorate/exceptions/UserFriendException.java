package ru.yandex.practicum.filmorate.exceptions;

public class UserFriendException extends Exception {
    private final String message;

    public UserFriendException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
