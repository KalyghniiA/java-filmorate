package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;
import java.util.List;


public interface FilmService {
    Film post(Film film);

    Film get(int id);

    void delete(int id);

    Collection<Film> getAll();

    Film put(Film film);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getPopular(int count);
}
