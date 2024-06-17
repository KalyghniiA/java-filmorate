package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaExtractor implements ResultSetExtractor<Mpa> {
    @Override
    public Mpa extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.next()) {
            return null;
        }
        return new Mpa(rs.getInt("RATING_ID"), rs.getString("NAME"));
    }
}
