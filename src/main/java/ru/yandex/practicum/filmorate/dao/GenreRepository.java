package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

public interface GenreRepository {
    Optional<Genre> getGenreById(int genreId);

    Set<Genre> getGenresById(List<Integer> genreIds);

    List<Genre> getGenres();

    Set<Genre> getGenresByFilm(int filmId);

    Map<Integer, Set<Genre>> getGenresByFilms(Collection<Integer> filmsId);
}
