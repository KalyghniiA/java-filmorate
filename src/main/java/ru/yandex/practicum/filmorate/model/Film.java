package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.filmEnum.Genre;
import ru.yandex.practicum.filmorate.util.filmEnum.Rating;
import ru.yandex.practicum.filmorate.util.validateAnotation.dateRelease.ValidDateRelease;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Передана пустая строка или пробелы")
    private final String name;
    @NotBlank(message = "Передано пустое значение описания фильма")
    @Size(max = 200, message = "Количество символов в описании не валидно")
    private final String description;
    @NotNull(message = "Передано пустое значение даты релиза фильма")
    @ValidDateRelease
    private final LocalDate releaseDate;
    @NotNull(message = "Передано пустое значение длительности фильма")
    @Positive(message = "Передано отрицательное значение длительности фильма")
    private final Integer duration;
    @NotNull(message = "Не указан жанр, либо указан неверно")
    private final Genre genre;
    @NotNull(message = "Не указан рейтинг, либо указан не верно")
    private final Rating rating;
    private Set<Integer> likes = new HashSet<>();
}
