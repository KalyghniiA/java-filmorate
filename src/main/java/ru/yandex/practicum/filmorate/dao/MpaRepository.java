package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaRepository {
    List<Mpa> getRatings();

    Mpa getRatingById(int ratingId);
}
