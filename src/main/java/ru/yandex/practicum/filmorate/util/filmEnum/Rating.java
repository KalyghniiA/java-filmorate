package ru.yandex.practicum.filmorate.util.filmEnum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Rating {
    G("G"),
    PG("PG"),
    @JsonProperty("PG-13")
    PG13("PG-13"),
    R("R"),
    @JsonProperty("NC-17")
    NC17("NC-17");

    @Getter
    private String title;
}
