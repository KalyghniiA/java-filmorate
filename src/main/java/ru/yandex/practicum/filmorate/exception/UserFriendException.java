package ru.yandex.practicum.filmorate.exception;

public class UserFriendException extends RuntimeException {
    private final String message;

    public UserFriendException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
