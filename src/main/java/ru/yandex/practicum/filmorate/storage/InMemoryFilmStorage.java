package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int currentId = 1;
    private final Map<Integer, Film> filmsStorage = new HashMap<>();

    @Override
    public Film add(Film film) {
        film.setId(currentId++);
        filmsStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(int id) {
        filmsStorage.remove(id);
    }

    @Override
    public Film put(Film film) {
        filmsStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(int id) {
        return filmsStorage.get(id);
    }

    @Override
    public Collection<Film> getAll() {
        return filmsStorage.values();
    }

    @Override
    public void addLike(int idFilm, int idUser) {
        filmsStorage.get(idFilm).getLikes().add(idUser);
    }

    @Override
    public void removeLike(int idFilm, int idUser) {
        Film film = filmsStorage.get(idFilm);
        film.getLikes().remove(idUser);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return filmsStorage.values().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
