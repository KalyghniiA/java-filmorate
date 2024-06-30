package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;


import java.util.*;
import java.util.stream.Collectors;

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
        film.setDirectors(directorRepository.getDirectorsByFilm(id));
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

        filmRepository.update(film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильма с id %s нет в базе", filmId)));
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", userId)));
        likeRepository.addLike(filmId, userId);
        eventService.addEvent(new Event(userId, EventType.LIKE.name(), Operation.ADD.name(),
                filmId, System.currentTimeMillis()));
    }

    @Override
    public void removeLike(int filmId, int userId) {
        filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильма с id %s нет в базе", filmId)));
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", userId)));
        likeRepository.removeLike(filmId, userId);
        eventService.addEvent(new Event(userId, EventType.LIKE.name(), Operation.REMOVE.name(),
                filmId, System.currentTimeMillis()));
    }

    @Override
    public List<Film> getSearched(String query, String by) {
        return switch (by.toLowerCase().trim()) {
            case "title" -> fillingFilms(filmRepository.searchFilmIds(query, by));
            case "director" -> fillingFilms(filmRepository.searchFilmIds(query, by));
            case "title,director", "director,title" ->
                //TODO придумать нормальное разделение, что если параметра будет не 2, а много(в параметре нужен массив)
                    fillingFilms(filmRepository.searchFilmIds(query, by));
            default -> throw new ValidationException("Переданный параметр сортировки не поддерживается");
        };
    }

    @Override
    public List<Film> getPopular(Integer count, Integer genreId, Integer year) { //для разных запросов
        List<Film> films = filmRepository.getTopPopularWithFilter(count, year, genreId);
//        List<Film> films;
//        if (genreId == 0 && year == null) {
//            films = filmRepository.getTopPopularWithFilter(count, year, genreId);
//        } else if (year == null) {
//            films = filmRepository.getTopPopularWithFilter(count, year, genreId);
//        } else if (genreId == 0) {
//            films = filmRepository.getTopPopularWithFilter(count, year, genreId);
//        } else {
//            films = filmRepository.getTopPopularWithFilter(count, year, genreId);
//        }

        return films;
//        return fillingFilms(filmRepository.getTopPopularWithFilter(count, year, genreId));
    }

    @Override
    public List<Film> getFilmsToDirector(int directorId, String sortBy) {
        List<Film> films = switch (sortBy.toLowerCase().trim()) {
            case "year" -> fillingFilms(filmRepository.getFilmsToDirectorSortByYear(directorId));
            case "likes" -> fillingFilms(filmRepository.getFilmsToDirectorSortByLikes(directorId));
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
        return fillingFilms(filmRepository.getCommonFilms(userId, friendId));
    }


    @Override
    public List<Film> getRecommendations(int userId) {
        return fillingFilms(filmRepository.getFilmsById(filmRepository.getRecommendationFilmIdByUserId(userId)));
    }

    private List<Film> fillingFilms(List<Film> films) {
        List<Integer> filmsId = films.stream().map(Film::getId).toList();
        Map<Integer, Set<Genre>> genresForFilms = genreRepository.getGenresByFilms(filmsId);
        Map<Integer, Set<Director>> directorsForFilms = directorRepository.getDirectorsByFilms(filmsId);
        return films.stream()
                .peek(film -> {
                    if (genresForFilms.containsKey(film.getId())) {
                        film.setGenres(genresForFilms.get(film.getId()));
                    }
                    if (directorsForFilms.containsKey(film.getId())) {
                        film.setDirectors(directorsForFilms.get(film.getId()));
                    }
                })
                .collect(Collectors.toList());
    }
}
