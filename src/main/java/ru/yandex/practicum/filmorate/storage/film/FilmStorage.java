package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.EmptyBodyException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film) throws EmptyBodyException;
    Film delete(Integer id) throws NotFoundFilmException, EmptyBodyException;
    Film put(Film film) throws EmptyBodyException, NotFoundFilmException;
    Film get (Integer id) throws NotFoundFilmException, EmptyBodyException;
    Collection<Film> getAll();
}
