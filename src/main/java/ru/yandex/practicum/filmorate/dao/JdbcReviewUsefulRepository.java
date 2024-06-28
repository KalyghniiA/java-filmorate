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
    public Integer getUsefulToReview(int reviewId) {
        String sql = """
                SELECT
                        (SELECT COUNT(REVIEW_ID)
                         FROM REVIEWS_LIKES
                         WHERE IS_LIKE IS TRUE AND REVIEW_ID = :review_id) -
                        (SELECT COUNT(REVIEW_ID)
                         FROM REVIEWS_LIKES
                         WHERE IS_LIKE IS FALSE AND REVIEW_ID = :review_id) AS USEFUL;
                """;
        Map<String, Object> param = Map.of("review_id", reviewId);

        return jdbc.query(sql, param, new UsefulExtractor());
    }

    @Override
    public Map<Integer, Integer> getUsefulToReviews(List<Integer> reviewsId) {
        String sql = """
                SELECT
                    REVIEW_ID,
                    SUM(CASE WHEN IS_LIKE IS TRUE THEN 1 ELSE 0 END) -
                    SUM(CASE WHEN IS_LIKE IS FALSE THEN 1 ELSE 0 END) AS USEFUL
                FROM
                    REVIEWS_LIKES
                WHERE
                        REVIEW_ID IN (:reviews_id)
                GROUP BY
                    REVIEW_ID;
                """;
        Map<String, Object> param = Map.of("reviews_id", reviewsId);
        return jdbc.query(sql, param, new UsefulnessExtractor());
    }

    @Override
    public void addLikeToReview(int reviewId, int userId) {
        String sqlDelete = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id AND USER_ID = :user_id;";
        String sqlInsert = "INSERT INTO REVIEWS_LIKES(review_id, user_id, is_like) VALUES ( :review_id, :user_id, true );";
        Map<String, Object> param = Map.of("review_id", reviewId, "user_id", userId);
        jdbc.update(sqlDelete, param);
        jdbc.update(sqlInsert, param);
    }

    @Override
    public void addDislikeToReview(int reviewId, int userId) {
        String sqlDelete = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id AND USER_ID = :user_id;";
        String sqlInsert = "INSERT INTO REVIEWS_LIKES(review_id, user_id, is_like) VALUES ( :review_id, :user_id, false );";
        Map<String, Object> param = Map.of("review_id", reviewId, "user_id", userId);
        jdbc.update(sqlDelete, param);
        jdbc.update(sqlInsert, param);
    }

    @Override
    public void deleteLikeToReview(int reviewId, int userId) {
        String sql = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id AND USER_ID = :user_id;";
        Map<String, Object> param = Map.of("review_id", reviewId, "user_id", userId);
        jdbc.update(sql, param);
    }

    @Override
    public void deleteDislikeToReview(int reviewId, int userId) {
        String sql = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :review_id AND USER_ID = :user_id AND IS_LIKE IS FALSE;";
        Map<String, Object> param = Map.of("review_id", reviewId, "user_id", userId);
        jdbc.update(sql, param);
    }
}
