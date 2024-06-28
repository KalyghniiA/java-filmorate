package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class GenresForFilmExtractor implements ResultSetExtractor<Set<Genre>> {
    @Override
    public Set<Genre> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));

        while (rs.next()) {
            genres.add(new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME")));
        }

        return genres;
    }
}
