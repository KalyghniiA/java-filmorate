package ru.yandex.practicum.filmorate.util;

public enum Genre {
    COMEDY("Комедия"),
    THRILLER("Ужасы"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private String title;

    Genre(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
