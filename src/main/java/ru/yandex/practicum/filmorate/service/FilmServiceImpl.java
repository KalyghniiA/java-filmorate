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

import java.util.*;

@Service

public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final LikeRepository likeRepository;


    @Autowired
    public FilmServiceImpl(FilmRepository filmRepository,
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
                film.setMpa(mpaRepository.getRatingById(film.getMpa().getId()));
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException(String.format("Рейтинга с id %s нет в базе", film.getMpa().getId()));
        }

        if (!film.getGenres().isEmpty()) {
            Set<Genre> genres = Set.copyOf(genreRepository.getGenresById(film.getGenres().stream().map(Genre::getId).toList()));
            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Одного из жанров нет в базе");
            }

            film.setGenres(genres);
        }


        return filmRepository.save(film);
    }

    @Override
    public Film get(int id) {
        return filmRepository.getById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Фильма с id %s нет в базе", id))
                );
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
        filmRepository.getById(film.getId())
                .orElseThrow(
                        () -> new NotFoundException(String.format("Фильма с id %s нет в базе", film.getId()))
                );

        try {
            if (film.getMpa() != null) {
                mpaRepository.getRatingById(film.getMpa().getId());
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException(String.format("Рейтинга с id %s нет в базе", film.getMpa().getId()));
        }

        if (!film.getGenres().isEmpty()) {
            Set<Genre> genres = Set.copyOf(genreRepository.getGenresById(film.getGenres().stream().map(Genre::getId).toList()));
            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Одного из жанров нет в базе");
            }
            film.setGenres(genres);
        }
        filmRepository.update(film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        likeRepository.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        likeRepository.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
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
        Genre genre = genreRepository.getGenreById(genreId);
        if (genre == null) {
            throw new NotFoundException(String.format("Жанра с id %s нет в базе", genreId));
        }
        return genre;
    }

    @Override
    public Mpa getRatingById(int ratingId) {
        Mpa mpa = mpaRepository.getRatingById(ratingId);
        if (mpa == null) {
            throw new NotFoundException(String.format("Рейтинга с id %s нет в базе", ratingId));
        }
        return mpa;
    }
}
