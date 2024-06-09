package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {
    Optional<Film> getById(int id);

    Film save(Film film);

    void delete(int id);

    Collection<Film> getAll();

    void addLike(int idFilm, int idUser);

    void removeLike(int idFilm, int idUser);

    Collection<Film> getTopPopular(int count);
}