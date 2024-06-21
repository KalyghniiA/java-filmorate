package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DirectorsForFilmExtractor implements ResultSetExtractor<Set<Director>> {
    @Override
    public Set<Director> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Set<Director> directors = new HashSet<>();
        while (rs.next()) {
            Director director = new Director();
            director.setId(rs.getInt("DIRECTOR_ID"));
            director.setName(rs.getString("NAME"));
            directors.add(director);
        }

        return directors;
    }
}
