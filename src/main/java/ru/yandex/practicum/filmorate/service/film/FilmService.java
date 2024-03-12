package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EmptyBodyException;
import ru.yandex.practicum.filmorate.exceptions.LikeException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
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

    public Film postFilm(Film film) throws EmptyBodyException {
        return filmStorage.add(film);
    }

    public Film getFilm(Integer id) throws NotFoundFilmException, EmptyBodyException {
        return filmStorage.get(id);
    }

    public Film deleteFilm(Integer id) throws NotFoundFilmException, EmptyBodyException {
        return filmStorage.delete(id);
    }

    public Collection<Film> getAllFilm() {
        return filmStorage.getAll();
    }

    public Film putFilm(Film film) throws NotFoundFilmException, EmptyBodyException {
        return filmStorage.put(film);
    }

    public Film addLike(Integer idFilm, Integer idUser) throws NotFoundFilmException, EmptyBodyException, NotFoundUserException, LikeException {
        Film film = filmStorage.get(idFilm);
        User user = userStorage.get(idUser);
        Set<User> likes = film.getLikes();

        if (likes.contains(user)) {
            throw new LikeException(String.format("Лайк пользователем %s уже поставлен", idUser));
        } else {
            likes.add(user);
        }

        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) throws
            NotFoundFilmException,
            EmptyBodyException,
            NotFoundUserException,
            LikeException {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);
        Set<User> likes = film.getLikes();

        if (likes.contains(user)) {
            likes.remove(user);
        } else {
            throw new LikeException(String.format("Лайка от пользователя %s нет", userId));
        }

        return film;
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
