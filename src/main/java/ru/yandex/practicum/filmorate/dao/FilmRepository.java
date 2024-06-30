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

    List<Film> getFilmsById(List<Integer> filmsId);

    List<Film> getTopPopularWithFilter(Integer count, Integer year, Integer genreId);

    List<Film> getFilmsToDirectorSortByYear(int directorId);

    List<Film> getFilmsToDirectorSortByLikes(int directorId);

    List<Film> searchFilmIds(String query, String by);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Integer> getRecommendationFilmIdByUserId(int userId);
}
