package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<Genre> getGenres() {
        return genreRepository.getGenres();
    }

    @Override
    public Genre getGenreById(int genreId) {
        return genreRepository.getGenreById(genreId).orElseThrow(() -> new NotFoundException(String.format("Жанра с id %s нет в базе", genreId)));
    }
}
