package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreExtractor implements ResultSetExtractor<Genre> {
    @Override
    public Genre extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.next()) {
            return null;
        }
        return new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME"));
    }
}
