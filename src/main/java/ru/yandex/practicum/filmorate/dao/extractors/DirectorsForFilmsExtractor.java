package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DirectorsForFilmsExtractor implements ResultSetExtractor<Map<Integer, Set<Director>>> {
    @Override
    public Map<Integer, Set<Director>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Set<Director>> directorsForFilms = new HashMap<>();
        while (rs.next()) {
            Integer filmId = rs.getInt("FILM_ID");
            Set<Director> directors = directorsForFilms.computeIfAbsent(filmId, k -> new HashSet<>());
            Director director = new Director();
            director.setId(rs.getInt("DIRECTOR_ID"));
            director.setName(rs.getString("NAME"));
            directors.add(director);
        }

        return directorsForFilms;
    }
}
