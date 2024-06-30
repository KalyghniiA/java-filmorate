package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;


import java.util.*;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final LikeRepository likeRepository;
    private final DirectorRepository directorRepository;
    private final UserRepository userRepository;
    private final EventService eventService;


    @Autowired
    public FilmServiceImpl(FilmRepository filmRepository,
                           GenreRepository genreRepository,
                           MpaRepository mpaRepository,
                           LikeRepository likeRepository,
                           DirectorRepository directorRepository,
                           UserRepository userRepository, EventService eventService) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.likeRepository = likeRepository;
        this.directorRepository = directorRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
    }

    @Override
    public Film post(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(
                    mpaRepository.getById(film.getMpa().getId()).orElseThrow(() -> new ValidationException("Рейтинга нет в базе"))
            );
        }

        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = genreRepository.getById(film.getGenres().stream().map(Genre::getId).toList());
            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Одного из жанров нет в базе");
            }

            film.getGenres().clear();
            film.getGenres().addAll(genres);
        }

        if (!film.getDirectors().isEmpty()) {
            List<Director> directors = directorRepository.getById(film.getDirectors().stream().map(Director::getId).toList());
            if (directors.size() != film.getDirectors().size()) {
                throw new ValidationException("Одного из режиссеров нет в базе");
            }
            film.getDirectors().clear();
            film.getDirectors().addAll(directors);
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
    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film put(Film film) {
        filmRepository.getById(film.getId())
                .orElseThrow(
                        () -> new NotFoundException(String.format("Фильма с id %s нет в базе", film.getId()))
                );

        if (film.getMpa() != null) {
            film.setMpa(
                    mpaRepository.getById(film.getMpa().getId()).orElseThrow(() -> new ValidationException("Рейтинга нет в базе"))
            );
        }

        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = genreRepository.getById(film.getGenres().stream().map(Genre::getId).toList());
            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Одного из жанров нет в базе");
            }
            film.getGenres().clear();
            film.getGenres().addAll(genres);
        }

        if (!film.getDirectors().isEmpty()) {
            List<Director> directors = directorRepository.getById(film.getDirectors().stream().map(Director::getId).toList());
            if (directors.size() != film.getDirectors().size()) {
                throw new ValidationException("Одного из режиссеров нет в базе");
            }

            film.getDirectors().clear();
            film.getDirectors().addAll(directors);
        }

        filmRepository.update(film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильма с id %s нет в базе", filmId)));
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", userId)));
        likeRepository.add(filmId, userId);
        eventService.addEvent(new Event(userId, EventType.LIKE.name(), Operation.ADD.name(),
                filmId));
    }

    @Override
    public void removeLike(int filmId, int userId) {
        filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильма с id %s нет в базе", filmId)));
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", userId)));
        likeRepository.remove(filmId, userId);
        eventService.addEvent(new Event(userId, EventType.LIKE.name(), Operation.REMOVE.name(),
                filmId));
    }

    @Override
    public List<Film> getSearched(String query, String by) {
        return switch (by.toLowerCase().trim()) {
            case "title" -> filmRepository.searchFilmIds(query, by);
            case "director" -> filmRepository.searchFilmIds(query, by);
            case "title,director", "director,title" ->
                //TODO придумать нормальное разделение, что если параметра будет не 2, а много(в параметре нужен массив)
                    filmRepository.searchFilmIds(query, by);
            default -> throw new ValidationException("Переданный параметр сортировки не поддерживается");
        };
    }

    @Override
    public List<Film> getPopular(Integer count, Integer genreId, Integer year) { //для разных запросов
        return filmRepository.getTopPopularWithFilter(count, year, genreId);
    }

    @Override
    public List<Film> getFilmsToDirector(int directorId, String sortBy) {
        List<Film> films = switch (sortBy.toLowerCase().trim()) {
            case "year" -> filmRepository.getFilmsToDirectorSortByYear(directorId);
            case "likes" -> filmRepository.getFilmsToDirectorSortByLikes(directorId);
            default -> throw new ValidationException("Переданный параметр сортировки не поддерживается");
        };

        if (films.isEmpty()) {
            throw new NotFoundException("по данным параметрам фильмов нет");
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", userId)));
        userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId)));
        //требуется делать отдельный метод для получения списка пользователей по айди что бы уменьшить обращения к базе?
        return filmRepository.getCommonFilms(userId, friendId);
    }


    @Override
    public List<Film> getRecommendations(int userId) {
        return filmRepository.getFilmsById(filmRepository.getRecommendationFilmIdByUserId(userId));
    }


}
