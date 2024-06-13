package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Optional<Film> getById(int id);

    Film save(Film film);

    Film update(Film film);

    void delete(int id);

    List<Film> getAll();

    List<Film> getTopPopular(int count);

    List<Film> getFilmsByGenre(int genreId);

    List<Film> getFilmsByRating(int ratingId);

}
