package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.UsefulExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.UsefulnessExtractor;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcReviewUsefulRepository implements ReviewUsefulRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Integer getUseful(int reviewId) {
        String sql = """
                SELECT SUM(IS_LIKE) AS USEFUL FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id;
                """;
        Map<String, Object> param = Map.of("review_id", reviewId);

        return jdbc.query(sql, param, new UsefulExtractor());
    }

    @Override
    public Map<Integer, Integer> getUseful(List<Integer> reviewsId) {
        String sql = """
                SELECT SUM(IS_LIKE) AS USEFUL, REVIEW_ID
                FROM REVIEWS_LIKES
                WHERE REVIEW_ID IN (:reviews_id)
                group by REVIEW_ID
                ORDER BY USEFUL DESC;
                """;
        Map<String, Object> param = Map.of("reviews_id", reviewsId);
        return jdbc.query(sql, param, new UsefulnessExtractor());
    }

    @Override
    public void addLike(int reviewId, int userId) {
        String sqlDelete = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id AND USER_ID = :user_id;";
        String sqlInsert = "INSERT INTO REVIEWS_LIKES(review_id, user_id, is_like) VALUES ( :review_id, :user_id, 1 );";
        Map<String, Object> param = Map.of("review_id", reviewId, "user_id", userId);
        jdbc.update(sqlDelete, param);
        jdbc.update(sqlInsert, param);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        String sqlDelete = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id AND USER_ID = :user_id;";
        String sqlInsert = "INSERT INTO REVIEWS_LIKES(review_id, user_id, is_like) VALUES ( :review_id, :user_id, -1 );";
        Map<String, Object> param = Map.of("review_id", reviewId, "user_id", userId);
        jdbc.update(sqlDelete, param);
        jdbc.update(sqlInsert, param);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        String sql = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id AND USER_ID = :user_id;";
        Map<String, Object> param = Map.of("review_id", reviewId, "user_id", userId);
        jdbc.update(sql, param);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        String sql = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id AND USER_ID = :user_id AND IS_LIKE = -1;";
        Map<String, Object> param = Map.of("review_id", reviewId, "user_id", userId);
        jdbc.update(sql, param);
    }
}
