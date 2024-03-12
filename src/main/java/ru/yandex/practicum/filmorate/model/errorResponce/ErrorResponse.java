package ru.yandex.practicum.filmorate.model.errorResponce;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {
    private final String error;
    private final String description;
}
