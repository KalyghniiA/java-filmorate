package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.GenreExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.GenresForFilmExtractor;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;


    @Override
    public Optional<Genre> getById(int genreId) {
        String sql = "SELECT GENRE_ID, NAME FROM GENRES WHERE GENRE_ID = :genre_id";
        Map<String, Object> param = Map.of("genre_id", genreId);
        return Optional.ofNullable(jdbc.query(sql, param, new GenreExtractor()));
    }

    @Override
    public List<Genre> getById(List<Integer> genresId) {
        String sql = "SELECT GENRE_ID, NAME FROM GENRES WHERE GENRE_ID IN ( :genres_id )";
        Map<String, Object> param = Map.of("genres_id", genresId);
        return jdbc.query(sql, param, new GenresForFilmExtractor());
    }

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT GENRE_ID AS ID, NAME FROM GENRES;";
        return jdbc.query(sql, new GenreRowMapper());
    }
}
