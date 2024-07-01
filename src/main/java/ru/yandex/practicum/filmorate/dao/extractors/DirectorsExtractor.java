package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DirectorsExtractor implements ResultSetExtractor<List<Director>> {
    @Override
    public List<Director> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Director> directors = new ArrayList<>();
        while (rs.next()) {
            Director director = new Director();
            director.setId(rs.getInt("DIRECTOR_ID"));
            director.setName(rs.getString("NAME"));
            directors.add(director);
        }
        return directors;
    }
}
