package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Parameter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Optional<Film> getById(int id);

    Film save(Film film);

    Film update(Film film);

    void delete(int id);

    Collection<Film> getAll();

    void addLike(int idFilm, int idUser);

    void removeLike(int idFilm, int idUser);

    Collection<Film> getTopPopular(int count);

    List<Parameter> getGenres();

    Parameter getGenreById(int genreId);

    Collection<Film> getFilmsByGenre(int genreId);

    List<Parameter> getRatings();

    Parameter getRatingById(int ratingId);

    Collection<Film> getFilmsByRating(int ratingId);

    boolean checkAvailabilityFilm(int filmId);

    boolean checkAvailabilityLike(int filmId, int userId);

    boolean checkAvailabilityGenreId(List<Genre> genres);

    boolean checkAvailabilityGenreId(int genreId);

    boolean checkAvailabilityRatingId(Mpa rating);

    boolean checkAvailabilityRatingId(int ratingIg);
}
