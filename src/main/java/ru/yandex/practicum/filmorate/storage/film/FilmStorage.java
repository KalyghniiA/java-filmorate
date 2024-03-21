package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.EmptyBodyException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film) throws EmptyBodyException;

    Film delete(Integer id) throws NotFoundException, EmptyBodyException;

    Film put(Film film) throws EmptyBodyException, NotFoundException;

    Film get(Integer id) throws NotFoundException, EmptyBodyException;

    Collection<Film> getAll();

    Film addLike(Integer idFilm, Integer idUser);

    Film removeLike(Integer idFilm, Integer idUser);

    Collection<Film> getPopular(int count);
}
