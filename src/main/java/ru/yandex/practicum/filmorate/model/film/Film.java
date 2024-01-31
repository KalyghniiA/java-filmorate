package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.util.validateAnotation.dateRelease.ValidDateRelease;
import ru.yandex.practicum.filmorate.util.validateAnotation.descriptionLength.ValidDescriptionLength;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;


@Data
public class Film {
    private Integer id;
    @NonNull
    @NotNull(message = "Передано пустое значение названия фильма")
    @NotBlank(message = "Передана пустая строка или пробелы")
    @NotEmpty(message = "Передана пустая строка")
    private final String name;
    @NonNull
    @NotNull(message = "Передано пустое значение описания фильма")
    @ValidDescriptionLength
    private final String description;
    @NonNull
    @NotNull(message = "Передано пустое значение даты релиза фильма")
    @ValidDateRelease
    private final LocalDate releaseDate;
    @NonNull
    @NotNull(message = "Передано пустое значение длительности фильма")
    @Positive(message = "Передано отрицательное значение длительности фильма")
    private final Integer duration;
}
