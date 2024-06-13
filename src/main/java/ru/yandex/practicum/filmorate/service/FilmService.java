package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


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

    Collection<Film> getPopular(int count);

    List<Genre> getGenres();

    List<Mpa> getRatings();

    Genre getGenreById(int genreId);

    Mpa getRatingById(int ratingId);

    Collection<Film> getOfRating(int ratingId);

    Collection<Film> getOfGenre(int genreId);
}
