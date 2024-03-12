package ru.yandex.practicum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

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
        return filmService.getAllFilm();
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable("id") Integer id) throws NotFoundFilmException, EmptyBodyException {
        return filmService.getFilm(id);
    }

    @PostMapping(value = "/films", headers = "content-type=application/json")
    public Film postFilm(@RequestBody @Valid Film film) throws EmptyBodyException {
        return filmService.postFilm(film);
    }

    @PutMapping(value = "/films")
    public Film putFilm(@RequestBody @Valid Film film) throws NotFoundFilmException, EmptyBodyException {
        return filmService.putFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId) throws
            NotFoundFilmException,
            EmptyBodyException,
            LikeException,
            NotFoundUserException {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film removeLike(@PathVariable Integer id,@PathVariable Integer userId) throws
            NotFoundFilmException,
            EmptyBodyException,
            LikeException,
            NotFoundUserException {
        return filmService.removeLike(id, userId);
    }

    @GetMapping(value = "/films/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping(value = "/test")
    public Film test(@RequestBody @Valid Film film) {
        return film;
    }
}
