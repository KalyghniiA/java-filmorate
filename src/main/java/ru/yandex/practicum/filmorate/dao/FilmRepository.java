package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Optional<Film> getById(int id);

    Film save(Film film);

    void update(Film film);

    void delete(int id);

    List<Film> getAll();

    List<Film> getTopPopular(Integer count);

    List<Film> getPopularFilmsByYearAndGenre(Integer count, Integer year, Integer genreId);

    List<Film> getPopularFilmsByYear(Integer count, Integer year);

    List<Film> getPopularFilmsByGenre(Integer count, Integer genreId);

    List<Film> getFilmsToDirectorSortByYear(int directorId);

    List<Film> getFilmsToDirectorSortByLikes(int directorId);
}
