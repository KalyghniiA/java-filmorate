package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcLikeRepository implements LikeRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void addLike(int filmId, int userId) {
        Map<String, Object> param = Map.of("film_id", filmId, "user_id", userId);
        String sql = "MERGE INTO LIKES(film_id, user_id) VALUES ( :film_id, :user_id );";
        jdbc.update(sql, param);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Map<String, Object> param = Map.of("film_id", filmId, "user_id", userId);
        String sql = "DELETE FROM LIKES WHERE FILM_ID = :film_id AND USER_ID = :user_id;";
        jdbc.update(sql, param);
    }
}
