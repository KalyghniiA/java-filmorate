package ru.yandex.practicum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmControllerException;
import ru.yandex.practicum.filmorate.model.film.Film;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class FilmController {
    private Map<Integer,Film> films = new HashMap<>();
    private Integer nextId = 1;

    @GetMapping(value = "/films")
    public Collection<Film> getFilms() {
        return films.values();
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable("id") String id) throws FilmControllerException {
        Integer idKey;
        try {
            idKey = Integer.valueOf(id);
        } catch (NumberFormatException e) {
            throw new FilmControllerException(e.getMessage());
        }
        if (!films.containsKey(idKey)) {
            throw new FilmControllerException("Данного фильма нет в базе");
        }
        Film film = films.get(idKey);
        if (film == null) {
            throw new FilmControllerException("Произошла ошибка в базе, попробуйте проверить id");
        }

        return film;
    }

    @PostMapping(value = "/films", headers = "content-type=application/json")
    public Film postFilm(@RequestBody @Valid Film film) throws FilmControllerException {
        if (film == null) {
            throw new FilmControllerException("Отправлено пустое значение");
        }
        if (film.getId() != null && films.containsKey(film.getId())) {
            throw new FilmControllerException("Данный фильм уже добавлен в базу, если требуется заменить, то использовать метод PUT");
        }

        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film putFilm(@RequestBody @Valid Film film) throws FilmControllerException {
        if (film == null) {
            throw new FilmControllerException("Отправлено пустое значение");
        }
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new FilmControllerException("Данного фильма нет в базе, если требуется добавить фильм, то требуется использоваться метод POST");
        }

        films.put(film.getId(), film);
        return film;
    }
}
//добавить логи
//рассмотреть возможность создание id на момент добавления фильма