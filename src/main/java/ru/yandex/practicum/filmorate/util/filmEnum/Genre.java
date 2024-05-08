package ru.yandex.practicum.filmorate.util.filmEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Genre {
    COMEDY("COMEDY"),
    THRILLER("THRILLER"),
    DRAMA("DRAMA"),
    CARTOON("CARTOON"),
    DOCUMENTARY("DOCUMENTARY"),
    ACTION("ACTION");

    @Getter
    private String title;
}
