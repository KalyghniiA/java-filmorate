package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorRepository {
    Optional<Director> getDirectorById(int directorId);

    List<Director> getDirectorsById(List<Integer> directorsId);

    Set<Director> getDirectorsByFilm(int filmId);

    Map<Integer, Set<Director>> getDirectorsByFilms(List<Integer> filmsId);

    List<Director> getDirectors();

    Director createDirector(Director director);

    void updateDirector(Director director);

    void deleteDirector(int directorId);
}
