package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dao.LikeRepository;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service

public class ImplFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final LikeRepository likeRepository;


    @Autowired
    public ImplFilmService(FilmRepository filmRepository,
                           GenreRepository genreRepository,
                           MpaRepository mpaRepository,
                           LikeRepository likeRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public Film post(Film film) {
        try {
            if (film.getMpa() != null) {
                mpaRepository.getRatingById(film.getMpa().getId());
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException(String.format("Рейтинга с id %s нет в базе", film.getMpa().getId()));
        }

        try {
            if (!film.getGenres().isEmpty()) {
                for (Genre genre: film.getGenres()) {
                    genreRepository.getGenreById(genre.getId());
                }
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("Одного из жанров нет в базе");
        }

        return filmRepository.save(film);
    }

    @Override
    public Film get(int id) {
        Optional<Film> film = filmRepository.getById(id);
        if (film.isEmpty()) {
            throw new NotFoundException(String.format("Фильма с id %s нет в базе", id));
        }
        return film.get();
    }

    @Override
    public void delete(int id) {
        filmRepository.delete(id);
    }

    @Override
    public Collection<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film put(Film film) {
        if (film.getId() == null) {
            return filmRepository.save(film);
        }
        if (filmRepository.getById(film.getId()).isEmpty()) {
            throw new NotFoundException(String.format("Фильма с id %s нет в базе", film.getId()));
        }
        try {
            if (film.getMpa() != null) {
                mpaRepository.getRatingById(film.getMpa().getId());
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException(String.format("Рейтинга с id %s нет в базе", film.getMpa().getId()));
        }

        try {
            if (!film.getGenres().isEmpty()) {
                for (Genre genre: film.getGenres()) {
                    genreRepository.getGenreById(genre.getId());
                }
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("Одного из жанров нет в базе");
        }
        return filmRepository.update(film);
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (likeRepository.getLikeByFilm(filmId).contains(userId)) {
            return;
        }

        likeRepository.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (!likeRepository.getLikeByFilm(filmId).contains(userId)) {
            return;
        }
        likeRepository.removeLike(filmId, userId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return filmRepository.getTopPopular(count);
    }

    @Override
    public List<Genre> getGenres() {
        return genreRepository.getGenres();
    }

    @Override
    public List<Mpa> getRatings() {
        return mpaRepository.getRatings();
    }

    @Override
    public Genre getGenreById(int genreId) {
        Optional<Genre> genre = genreRepository.getGenreById(genreId);
        if (genre.isEmpty()) {
            throw new NotFoundException(String.format("Жанра с id %s нет в базе", genreId));
        }
        return genre.get();
    }

    @Override
    public Mpa getRatingById(int ratingId) {
        Optional<Mpa> mpa = mpaRepository.getRatingById(ratingId);
        if (mpa.isEmpty()) {
            throw new NotFoundException(String.format("Рейтинга с id %s нет в базе", ratingId));
        }
        return mpa.get();
    }

    @Override
    public Collection<Film> getOfRating(int ratingId) {
        Optional<Mpa> mpa = mpaRepository.getRatingById(ratingId);
        if (mpa.isEmpty()) {
            throw new NotFoundException(String.format("Рейтинга с id %s нет в базе", ratingId));
        }
        try {
            return filmRepository.getFilmsByRating(ratingId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильмов с данным рейтингом нет");
        }
    }

    @Override
    public Collection<Film> getOfGenre(int genreId) {
        Optional<Genre> genre = genreRepository.getGenreById(genreId);
        if (genre.isEmpty()) {
            throw new NotFoundException(String.format("Жанра с id %s нет в базе", genreId));
        }
        try {
            return filmRepository.getFilmsByGenre(genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильмов с данным жанром нет в базе");
        }
    }

}
