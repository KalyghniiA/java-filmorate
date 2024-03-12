package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EmptyBodyException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int currentId = 1;
    private final Map<Integer, Film> filmsStorage = new HashMap<>();

    @Override
    public Film add(Film film) throws EmptyBodyException {
        if (film == null) throw new EmptyBodyException("Отправлено пустое значение");
        film.setId(currentId++);
        filmsStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Integer id) throws NotFoundFilmException, EmptyBodyException {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        if (!filmsStorage.containsKey(id)) throw new NotFoundFilmException("Фильма с данным id нет");

        return filmsStorage.remove(id);
    }

    @Override
    public Film put(Film film) throws EmptyBodyException, NotFoundFilmException {
        if (film == null) throw new EmptyBodyException("Передано пустое значение");
        if (film.getId() == null || !filmsStorage.containsKey(film.getId())) {
            throw new NotFoundFilmException("Данного фильма нет");
        }
        filmsStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(Integer id) throws NotFoundFilmException, EmptyBodyException {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        if (!filmsStorage.containsKey(id)) throw new NotFoundFilmException("Данного ильма нет в базе");
        return filmsStorage.get(id);
    }

    @Override
    public Collection<Film> getAll() {
        return filmsStorage.values();
    }
}
