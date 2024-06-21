package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final LikeRepository likeRepository;
    private final DirectorRepository directorRepository;


    @Autowired
    public FilmServiceImpl(FilmRepository filmRepository,
                           GenreRepository genreRepository,
                           MpaRepository mpaRepository,
                           LikeRepository likeRepository,
                           DirectorRepository directorRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.likeRepository = likeRepository;
        this.directorRepository = directorRepository;
    }

    @Override
    public Film post(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(
                    mpaRepository.getRatingById(film.getMpa().getId()).orElseThrow(() -> new ValidationException("Рейтинга нет в базе"))
            );
        }

        if (!film.getGenres().isEmpty()) {
            Set<Genre> genres = genreRepository.getGenresById(film.getGenres().stream().map(Genre::getId).toList());
            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Одного из жанров нет в базе");
            }

            film.setGenres(genres);
        }

        if (!film.getDirectors().isEmpty()) {
            Set<Director> directors = Set.copyOf(directorRepository.getDirectorsById(film.getDirectors().stream().map(Director::getId).toList()));
            if (directors.size() != film.getDirectors().size()) {
                throw new ValidationException("Одного из режиссеров нет в базе");
            }

            film.setDirectors(directors);
        }


        return filmRepository.save(film);
    }

    @Override
    public Film get(int id) {
        Film film = filmRepository.getById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Фильма с id %s нет в базе", id))
                );
        film.setGenres(genreRepository.getGenresByFilm(film.getId()));
        return film;
    }

    @Override
    public void delete(int id) {
        filmRepository.delete(id);
    }

    @Override
    public List<Film> getAll() {
        return fillingFilms(filmRepository.getAll());
    }

    @Override
    public Film put(Film film) {
        filmRepository.getById(film.getId())
                .orElseThrow(
                        () -> new NotFoundException(String.format("Фильма с id %s нет в базе", film.getId()))
                );

        if (film.getMpa() != null) {
            film.setMpa(
                    mpaRepository.getRatingById(film.getMpa().getId()).orElseThrow(() -> new ValidationException("Рейтинга нет в базе"))
            );
        }

        if (!film.getGenres().isEmpty()) {
            Set<Genre> genres = Set.copyOf(genreRepository.getGenresById(film.getGenres().stream().map(Genre::getId).toList()));
            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Одного из жанров нет в базе");
            }
            film.setGenres(genres);
        }

        if (!film.getDirectors().isEmpty()) {
            Set<Director> directors = Set.copyOf(directorRepository.getDirectorsById(film.getDirectors().stream().map(Director::getId).toList()));
            if (directors.size() != film.getDirectors().size()) {
                throw new ValidationException("Одного из режиссеров нет в базе");
            }

            film.setDirectors(directors);
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
        return fillingFilms(filmRepository.getTopPopular(count));
    }

    @Override
    public List<Film> getFilmsToDirector(int directorId, String sortBy) {
        switch (sortBy.toLowerCase().trim()) {
            case "year": return fillingFilms(filmRepository.getFilmsToDirectorSortByYear(directorId));
            case "likes": return fillingFilms(filmRepository.getFilmsToDirectorSortByLikes(directorId));
            default: throw new ValidationException("Переданный параметр сортировки не поддерживается");
        }
    }

    private List<Film> fillingFilms(List<Film> films) {
        List<Integer> filmsId = films.stream().map(Film::getId).toList();
        Map<Integer, Set<Genre>> genresForFilms = genreRepository.getGenresByFilms(filmsId);
        Map<Integer, Set<Director>> directorsForFilms = directorRepository.getDirectorsByFilms(filmsId);
        return films.stream()
                .peek(film -> {
                    film.setGenres(genresForFilms.get(film.getId()));
                    film.setDirectors(directorsForFilms.get(film.getId()));
                })
                .collect(Collectors.toList());
    }
}
