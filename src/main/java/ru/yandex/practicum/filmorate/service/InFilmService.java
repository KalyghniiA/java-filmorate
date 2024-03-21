package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class InFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InFilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film post(Film film) {
        return filmStorage.add(film);
    }

    @Override
    public Film get(int id) {
        Film film = filmStorage.get(id);
        if (film == null) {
            throw new NotFoundException(String.format("Фильма с id %s нет в базе", id));
        }
        return film;
    }

    @Override
    public void delete(int id) {
        filmStorage.delete(id);
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film put(Film film) {
        if (film.getId() == null) {
            throw new EmptyDataException("В переданных данных отсутствует параметр id");
        }
        //Просьба обратить внимание на проверку ниже, может данную логику убрать в storage??
        if (filmStorage.get(film.getId()) == null) {
            throw new NotFoundException(String.format("Фильма с id %s нет в базе", film.getId()));
        }
        return filmStorage.put(film);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);
        if (film == null) {
            throw new NotFoundException(String.format("Фильма с id %s нет в базе", filmId));
        }
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", userId));
        }

        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);
        if (film == null) {
            throw new NotFoundException(String.format("Фильма с id %s нет в базе", filmId));
        }
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", userId));
        }

        filmStorage.removeLike(filmId, userId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }
}
