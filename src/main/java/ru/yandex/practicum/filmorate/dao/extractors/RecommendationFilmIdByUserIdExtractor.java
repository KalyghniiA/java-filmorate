package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecommendationFilmIdByUserIdExtractor implements ResultSetExtractor<List<Integer>> {

    @Override
    public List<Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Integer> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getInt("film_id"));
        }
        return result;
    }
}