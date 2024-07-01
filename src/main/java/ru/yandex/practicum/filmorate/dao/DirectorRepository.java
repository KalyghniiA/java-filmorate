package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    Optional<Director> getById(int directorId);

    List<Director> getById(List<Integer> directorsId);

    List<Director> get();

    Director create(Director director);

    void update(Director director);

    void delete(int directorId);
}
