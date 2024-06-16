package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Mpa> getRatings() {
        String sql = "SELECT RATING_ID AS ID, NAME FROM RATINGS;";
        List<Mpa> ratings = new ArrayList<>();
        SqlRowSet rs = jdbc.queryForRowSet(sql, Map.of());
        while (rs.next()) {
            Mpa mpa = new Mpa(rs.getInt("ID"), rs.getString("NAME"));
            System.out.println(mpa);
            ratings.add(new Mpa(rs.getInt("ID"), rs.getString("NAME")));
        }
        return ratings;
    }

    @Override
    public Mpa getRatingById(int ratingId) {
        String sql = "SELECT RATING_ID, NAME FROM RATINGS WHERE RATING_ID = :rating_id";
        Map<String, Object> param = Map.of("rating_id", ratingId);
        return jdbc.queryForObject(sql, param, new MpaRowMapper());
    }
}
