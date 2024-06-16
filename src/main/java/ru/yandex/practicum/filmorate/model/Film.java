package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.validateAnotation.dateRelease.ValidDateRelease;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
    private Set<Genre> genres = new LinkedHashSet<>();
    private Mpa mpa;
    private Set<Integer> likes = new HashSet<>();
}
