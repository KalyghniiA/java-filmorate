package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidIdParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.model.Parameter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class InFilmService implements FilmService {
    private final FilmRepository filmStorage;

    @Autowired
    public InFilmService(FilmRepository filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Film post(Film film) {
        if (film.getMpa() != null && !filmStorage.checkAvailabilityRatingId(film.getMpa())) {
            throw new InvalidIdParameterException(String.format("Рейтинга с id %s нет в базе", film.getMpa().getId()));
        }
        if (!film.getGenres().isEmpty() && !filmStorage.checkAvailabilityGenreId(film.getGenres())) {
            throw new InvalidIdParameterException("Одного из жанров нет в базе");
        }
        return filmStorage.save(film);
    }

    @Override
    public Film get(int id) {
        try {
            Optional<Film> film = filmStorage.getById(id);
            return film.get();
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Фильма с id %s нет в базе", id));
        }
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
            return filmStorage.save(film);
        }
        if (!filmStorage.checkAvailabilityFilm(film.getId())) {
            throw new NotFoundException(String.format("Фильма с id %s нет в базе", film.getId()));
        }
        if (film.getMpa() != null && !filmStorage.checkAvailabilityRatingId(film.getMpa())) {
            throw new InvalidIdParameterException(String.format("Рейтинга с id %s нет в базе", film.getMpa().getId()));
        }
        if (!film.getGenres().isEmpty() && !filmStorage.checkAvailabilityGenreId(film.getGenres())) {
            throw new InvalidIdParameterException("Одного из жанров нет в базе");
        }
        return filmStorage.update(film);
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (filmStorage.checkAvailabilityLike(filmId, userId)) {
            return;
        }

        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (!filmStorage.checkAvailabilityLike(filmId, userId)) {
            return;
        }
        filmStorage.removeLike(filmId, userId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return filmStorage.getTopPopular(count);
    }

    @Override
    public List<Parameter> getGenres() {
        return filmStorage.getGenres();
    }

    @Override
    public List<Parameter> getRatings() {
        return filmStorage.getRatings();
    }

    @Override
    public Parameter getGenreById(int genreId) {
        if (!filmStorage.checkAvailabilityGenreId(genreId)) {
            throw new NotFoundException(String.format("Жанра с id %s нет в базе", genreId));
        }
        return filmStorage.getGenreById(genreId);
    }

    @Override
    public Parameter getRatingById(int ratingId) {
        if (!filmStorage.checkAvailabilityRatingId(ratingId)) {
            throw new NotFoundException(String.format("Рейтинга с id %s нет в базе", ratingId));
        }
        return filmStorage.getRatingById(ratingId);
    }

    @Override
    public Collection<Film> getOfRating(int ratingId) {
        if (!filmStorage.checkAvailabilityRatingId(ratingId)) {
            throw new InvalidIdParameterException(String.format("Рейтинга с id %s нет в базе", ratingId));
        }
        try {
            return filmStorage.getFilmsByRating(ratingId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильмов с данным рейтингом нет");
        }
    }

    @Override
    public Collection<Film> getOfGenre(int genreId) {
        if (!filmStorage.checkAvailabilityGenreId(genreId)) {
            throw new InvalidIdParameterException(String.format("Жанра с id %s нет в базе", genreId));
        }
        try {
            return filmStorage.getFilmsByGenre(genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильмов с данным жанром нет в базе");
        }
    }

}
