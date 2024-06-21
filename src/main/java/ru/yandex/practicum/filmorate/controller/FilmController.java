package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final GenreService genreService;
    private final MpaService mpaService;

    @Autowired
    public FilmController(FilmService filmService, GenreService genreService, MpaService mpaService) {
        this.filmService = filmService;
        this.genreService = genreService;
        this.mpaService = mpaService;
    }

    @GetMapping(value = "/films")
    public Collection<Film> getFilms() {
        log.info("Получен GET запрос на получение всех фильмов");
        Collection<Film> films = filmService.getAll();
        log.info("Отправлены фильмы");
        return films;
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable("id") Integer id) {
        log.info("Получен GET запрос на получение фильма");
        Film film = filmService.get(id);
        log.info(String.format("Отправлены данные фильма с id %s", film.getId()));
        return film;
    }

    @PostMapping(value = "/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film postFilm(@RequestBody @Valid Film film) {
        log.info("Получен POST запрос на добавление фильма");
        Film newFilm = filmService.post(film);
        log.info(String.format("добавлен фильм под id %s", film.getId()));
        return newFilm;
    }

    @PutMapping(value = "/films")
    public Film putFilm(@RequestBody @Valid Film film) {
        log.info("Получен PUT запрос на изменение фильма");
        Film newFilm = filmService.put(film);
        log.info(String.format("Фильм с id %s изменен", newFilm.getId()));
        return newFilm;
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Получен PUT запрос на добавление лайка");
        filmService.addLike(id, userId);
        log.info(String.format("Фильм с id %s изменен", id));
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Получен DELETE запрос на удаление лайка");
        filmService.removeLike(id, userId);
        log.info(String.format("Удален лайк у фильма с id %s", id));
    }

    @DeleteMapping(value = "/films/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Integer id) {
        log.info("Получен DELETE запрос на удаление фильма");
        filmService.delete(id);
        log.info(String.format("Удален фильм с id %s", id));
    }

    @GetMapping(value = "/films/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false) @Min(0) Integer count,
                                            @RequestParam(defaultValue = "0") Integer genreId,
                                            @RequestParam(required = false) @Min(1895) Integer year) {
        log.info("Получен GET запрос на получение популярных фильмов");
        Collection<Film> films = filmService.getPopular(count, genreId, year);
        log.info(String.format("Отправлены популярные фильмы в количестве %s", count));
        return films;
    }

    @GetMapping("/genres")
    public Collection<Genre> getGenres() {
        log.info("Получен GET запрос на получение списка жанров");
        Collection<Genre> genres = genreService.getGenres();
        log.info("Отправлены жанры");
        return genres;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        log.info(String.format("Получен GET запрос на получение имени жанра с id %s", id));
        Genre parameter = genreService.getGenreById(id);
        log.info(String.format("Отправлено имя жанра с id %s", id));
        return parameter;
    }

    @GetMapping("/mpa")
    public Collection<Mpa> getRating() {
        log.info("Получен GET запрос на получение списка рейтингов");
        Collection<Mpa> ratings = mpaService.getRatings();
        log.info("Отправлены рейтинги");
        return ratings;
    }

    @GetMapping("/mpa/{id}")
    public Mpa getRatingById(@PathVariable Integer id) {
        log.info(String.format("Получен GET запрос на получение рейтинга c id %s", id));
        Mpa parameter = mpaService.getRatingById(id);
        log.info(String.format("Отправлено название рейтинга c id  %s", id));
        return parameter;
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getFilmsByDirector(
            @PathVariable Integer directorId,
            @RequestParam String sortBy
    ) {
        log.info(String.format("Получен запрос на получение фильмов режиссера %s отсортированный по %s", directorId, sortBy));
        List<Film> films = filmService.getFilmsToDirector(directorId, sortBy);
        log.info("Отправлены фильмы режиссера");
        return films;
    }
}
