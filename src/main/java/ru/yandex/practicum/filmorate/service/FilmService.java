package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyBodyException;
import ru.yandex.practicum.filmorate.exception.EmptyDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film post(Film film) {
        if (film == null) throw new EmptyBodyException("Отправлено пустое значение");
        return filmStorage.add(film);
    }

    public Film get(Integer id) {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        Film film = filmStorage.get(id);
        if (film == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", id));
        return film;
    }

    public void delete(Integer id) {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        Film film = filmStorage.delete(id);
        if (film == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", id));
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film put(Film film) {
        if (film == null) throw new EmptyBodyException("Передано пустое значение");
        if (film.getId() == null) throw new EmptyDataException("В переданных данных отсутствует параметр id");
        //Просьба обратить внимание на проверку ниже, может данную логику убрать в storage??
        if (filmStorage.get(film.getId()) == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", film.getId()));
        return filmStorage.put(film);
    }

    public Film addLike(Integer idFilm, Integer idUser) {
        if (idFilm == null || idUser == null) throw new EmptyBodyException("Передано пустое значение");
        Film film = filmStorage.get(idFilm);
        User user = userStorage.get(idUser);
        if (film == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", idFilm));
        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", idUser));

        return filmStorage.addLike(idFilm, idUser);
    }

    public void removeLike(Integer idFilm, Integer idUser) {
        if (idFilm == null || idUser == null) throw new EmptyBodyException("передано пустое значение");
        Film film = filmStorage.get(idFilm);
        User user = userStorage.get(idUser);
        if (film == null) throw new NotFoundException(String.format("Фильма с id %s нет в базе", idFilm));
        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", idUser));

        filmStorage.removeLike(idFilm, idUser);
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }
}
