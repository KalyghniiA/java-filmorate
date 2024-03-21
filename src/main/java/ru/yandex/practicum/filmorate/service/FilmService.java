package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Film post(Film film);

    Film get(Integer id);

    void delete(Integer id);

    Collection<Film> getAll();

    Film put(Film film);

    Film addLike(Integer idFilm, Integer idUser);

    void removeLike(Integer idFilm, Integer idUser);

    Collection<Film> getPopular(int count);
}
