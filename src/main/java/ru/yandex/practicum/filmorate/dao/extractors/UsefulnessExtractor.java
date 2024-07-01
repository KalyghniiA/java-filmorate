package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UsefulnessExtractor implements ResultSetExtractor<Map<Integer, Integer>> {
    @Override
    public Map<Integer, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Integer> usefulness = new HashMap<>();

        while (rs.next()) {
            usefulness.put(rs.getInt("REVIEW_ID"), rs.getInt("USEFUL"));
        }

        return usefulness;
    }
}
