package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GenresForFilmsExtractor implements ResultSetExtractor<Map<Integer, List<Genre>>> {
    @Override
    public Map<Integer, List<Genre>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, List<Genre>> genresForFilms = new HashMap<>();
        while (rs.next()) {
            Integer filmId = rs.getInt("FILM_ID");
            List<Genre> genres = genresForFilms.computeIfAbsent(filmId, k -> new ArrayList<>());
            genres.add(new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME")));
        }

        return genresForFilms;
    }
}
