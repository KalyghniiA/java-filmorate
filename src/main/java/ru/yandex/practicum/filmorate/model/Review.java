package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    @JsonProperty("reviewId")
    private Integer id;
    @NotBlank(message = "Передана пустая строка или пробелы")
    private final String content;
    @JsonProperty("isPositive")
    @NotNull
    private final Boolean isPositive;
    @NotNull
    private final Integer userId;
    @NotNull
    private final Integer filmId;
    private Integer useful = 0;
}
