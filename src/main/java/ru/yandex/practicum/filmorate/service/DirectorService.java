package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    List<Director> getDirectors();

    Director getDirectorById(int directorId);

    Director saveDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int directorId);
}
