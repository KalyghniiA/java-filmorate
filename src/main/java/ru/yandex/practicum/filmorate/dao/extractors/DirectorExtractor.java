package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorExtractor implements ResultSetExtractor<Director> {
    @Override
    public Director extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.next()) {
            return null;
        }
        Director director = new Director();
        director.setId(rs.getInt("DIRECTOR_ID"));
        director.setName(rs.getString("NAME"));
        return director;
    }
}
