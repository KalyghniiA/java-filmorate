package ru.yandex.practicum.filmorate.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DuplicateDataException extends RuntimeException {
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
}
