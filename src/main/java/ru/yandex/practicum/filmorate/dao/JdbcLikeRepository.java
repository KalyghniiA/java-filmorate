package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcLikeRepository implements LikeRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void addLike(int filmId, int userId) {
        Map<String, Object> param = Map.of("film_id", filmId, "user_id", userId);
        String sql = "INSERT INTO LIKES(film_id, user_id) VALUES ( :film_id, :user_id );";
        jdbc.update(sql, param);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Map<String, Object> param = Map.of("film_id", filmId, "user_id", userId);
        String sql = "DELETE FROM LIKES WHERE FILM_ID = :film_id AND USER_ID = :user_id;";
        jdbc.update(sql, param);
    }

    @Override
    public List<Integer> getLikedFilmsByUserId(int userId) {
        String sql = """
                SELECT  LIKES.FILM_ID
                FROM LIKES
                WHERE USER_ID = :user_id
                """;
        Map<String, Object> param = Map.of("user_id", userId);
        SqlRowSet sqlRowSet = jdbc.queryForRowSet(sql, param);
        List<Integer> filmIds = new ArrayList<>();
        while (sqlRowSet.next()) {
            filmIds.add(sqlRowSet.getInt("film_id"));
        }
        return filmIds;
    }
}
