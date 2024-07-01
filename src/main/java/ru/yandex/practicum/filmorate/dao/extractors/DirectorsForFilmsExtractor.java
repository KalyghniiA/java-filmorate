package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DirectorsForFilmsExtractor implements ResultSetExtractor<Map<Integer, List<Director>>> {
    @Override
    public Map<Integer, List<Director>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, List<Director>> directorsForFilms = new HashMap<>();
        while (rs.next()) {
            Integer filmId = rs.getInt("FILM_ID");
            List<Director> directors = directorsForFilms.computeIfAbsent(filmId, k -> new ArrayList<>());
            Director director = new Director();
            director.setId(rs.getInt("DIRECTOR_ID"));
            director.setName(rs.getString("NAME"));
            directors.add(director);
        }

        return directorsForFilms;
    }
}
