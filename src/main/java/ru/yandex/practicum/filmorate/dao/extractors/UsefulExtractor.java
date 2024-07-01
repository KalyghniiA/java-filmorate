package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsefulExtractor implements ResultSetExtractor<Integer> {
    @Override
    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
        int useful = 0;
        if (rs.next()) {
            useful = rs.getInt("USEFUL");
        }
        return useful;
    }
}
