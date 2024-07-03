package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

public interface GenreRepository {
    Optional<Genre> getById(int genreId);

    List<Genre> getById(List<Integer> genreIds);

    List<Genre> getAll();
}
