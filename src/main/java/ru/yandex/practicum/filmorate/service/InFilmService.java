package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyBodyException;
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
        if (film == null) throw new EmptyBodyException("Отправлено пустое значение");
        return filmStorage.add(film);
    }

    @Override
    public Film get(Integer id) {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        Film film = filmStorage.get(id);
        if (film == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", id));
        return film;
    }

    @Override
    public void delete(Integer id) {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        Film film = filmStorage.delete(id);
        if (film == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", id));
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film put(Film film) {
        if (film == null) throw new EmptyBodyException("Передано пустое значение");
        if (film.getId() == null) throw new EmptyDataException("В переданных данных отсутствует параметр id");
        //Просьба обратить внимание на проверку ниже, может данную логику убрать в storage??
        if (filmStorage.get(film.getId()) == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", film.getId()));
        return filmStorage.put(film);
    }

    @Override
    public Film addLike(Integer idFilm, Integer idUser) {
        if (idFilm == null || idUser == null) throw new EmptyBodyException("Передано пустое значение");
        Film film = filmStorage.get(idFilm);
        User user = userStorage.get(idUser);
        if (film == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", idFilm));
        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", idUser));

        return filmStorage.addLike(idFilm, idUser);
    }

    @Override
    public void removeLike(Integer idFilm, Integer idUser) {
        if (idFilm == null || idUser == null) throw new EmptyBodyException("передано пустое значение");
        Film film = filmStorage.get(idFilm);
        User user = userStorage.get(idUser);
        if (film == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", idFilm));
        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", idUser));

        filmStorage.removeLike(idFilm, idUser);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }
}
