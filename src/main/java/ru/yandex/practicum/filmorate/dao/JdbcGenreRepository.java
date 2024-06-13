package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;


    @Override
    public Optional<Genre> getGenreById(int genreId) {
        String sql = "SELECT GENRE_ID, NAME FROM GENRES WHERE GENRE_ID = :genre_id";
        Map<String, Object> param = Map.of("genre_id", genreId);
        return Optional.ofNullable(jdbc.queryForObject(sql, param, new GenreRowMapper()));
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT GENRE_ID AS ID, NAME FROM GENRES;";
        List<Genre> genres = new ArrayList<>();
        SqlRowSet rs = jdbc.queryForRowSet(sql, Map.of());
        while (rs.next()) {
            genres.add(new Genre(rs.getInt("ID"), rs.getString("NAME")));
        }

        return genres;
    }

}
