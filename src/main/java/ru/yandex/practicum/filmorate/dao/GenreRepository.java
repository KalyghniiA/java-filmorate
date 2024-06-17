package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    Optional<Genre> getGenreById(int genreId);

    List<Genre> getGenresById(List<Integer> genreIds);

    List<Genre> getGenres();
}
