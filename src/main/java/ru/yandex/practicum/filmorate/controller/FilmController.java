package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Parameter;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
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
    public void removeLike(@PathVariable Integer id,@PathVariable Integer userId) {
        log.info("Получен DELETE запрос на удаление лайка");
        filmService.removeLike(id, userId);
        log.info(String.format("Удален лайк у фильма с id %s", id));
    }

    @GetMapping(value = "/films/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен GET запрос на получение популярных фильмов");
        Collection<Film> films = filmService.getPopular(count);
        log.info(String.format("Отправлены популярные фильмы в количестве %s", count));
        return films;
    }

    @GetMapping("/genres")
    public Collection<Parameter> getGenres() {
        log.info("Получен GET запрос на получение списка жанров");
        Collection<Parameter> genres = filmService.getGenres();
        log.info("Отправлены жанры");
        return genres;
    }

    @GetMapping("/genres/{id}")
    public Parameter getGenreById(@PathVariable Integer id) {
        log.info(String.format("Получен GET запрос на получение имени жанра с id %s", id));
        Parameter parameter = filmService.getGenreById(id);
        log.info(String.format("Отправлено имя жанра с id %s", id));
        return parameter;
    }

    @GetMapping("/mpa")
    public Collection<Parameter> getRating() {
        log.info("Получен GET запрос на получение списка рейтингов");
        Collection<Parameter> ratings = filmService.getRatings();
        log.info("Отправлены рейтинги");
        return ratings;
    }

    @GetMapping("/mpa/{id}")
    public Parameter getRatingById(@PathVariable Integer id) {
        log.info(String.format("Получен GET запрос на получение рейтинга c id %s", id));
        Parameter parameter = filmService.getRatingById(id);
        log.info(String.format("Отправлено название рейтинга c id  %s", id));
        return parameter;
    }
}
