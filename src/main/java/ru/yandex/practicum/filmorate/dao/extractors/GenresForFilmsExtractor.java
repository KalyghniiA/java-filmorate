package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GenresForFilmsExtractor implements ResultSetExtractor<Map<Integer, Set<Genre>>> {
    @Override
    public Map<Integer, Set<Genre>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Set<Genre>> genresForFilms = new HashMap<>();
        while (rs.next()) {
            Integer filmId = rs.getInt("FILM_ID");
            Set<Genre> genres = genresForFilms.computeIfAbsent(filmId, k -> new TreeSet<>(Comparator.comparingInt(Genre::getId)));
            genres.add(new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME")));
        }

        return genresForFilms;
    }
}
