package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film);

    void delete(int id);

    Film put(Film film);

    Film get(int id);

    Collection<Film> getAll();

    void addLike(int idFilm, int idUser);

    void removeLike(int idFilm, int idUser);

    Collection<Film> getPopular(int count);
}
