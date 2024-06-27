package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.ReviewExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.ReviewsExtractor;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Review> getById(int id) {
        String sql = """
                SELECT REVIEW_ID,
                       CONTENT,
                       IS_POSITIVE,
                       USER_ID,
                       FILM_ID
                FROM REVIEWS
                WHERE REVIEW_ID = :review_id;
                """;

        Map<String, Object> param = Map.of("review_id", id);

        return Optional.ofNullable(jdbc.query(sql, param, new ReviewExtractor()));
    }

    @Override
    public Review save(Review review) {
        KeyHolder kh = new GeneratedKeyHolder();
        String sql = """
                INSERT INTO REVIEWS(CONTENT, IS_POSITIVE, USER_ID, FILM_ID)
                VALUES (:content, :is_positive, :user_id, :film_id );
                """;
        Map<String, Object> param = Map.of(
                "content", review.getContent(),
                "is_positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId()
        );
        jdbc.update(sql, new MapSqlParameterSource().addValues(param), kh, new String[]{"review_id"});
        review.setId(kh.getKeyAs(Integer.class));
        return review;
    }

    @Override
    public void update(Review review) {
        String sql = """
                UPDATE REVIEWS
                    SET CONTENT = :content,
                        IS_POSITIVE = :is_positive,
                        USER_ID = :user_id,
                        FILM_ID = :film_id
                WHERE REVIEW_ID = :review_id;
                """;
        Map<String, Object> param = Map.of(
                "content", review.getContent(),
                "is_positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "review_id", review.getId()
                );

        jdbc.update(sql, param);
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID = :review_id;";
        Map<String, Object> param = Map.of("review_id", id);

        jdbc.update(sql, param);
    }

    @Override
    public List<Review> getReviews(int count) {
        //не уверен в получении полезности
        String sql = """
                SELECT REVIEW_ID,
                       CONTENT,
                       IS_POSITIVE,
                       USER_ID,
                       FILM_ID,
                       ((SELECT COUNT(REVIEWS_LIKES.REVIEW_ID)
                         FROM REVIEWS_LIKES
                         WHERE IS_LIKE IS TRUE AND REVIEWS_LIKES.REVIEW_ID = REVIEWS.REVIEW_ID) -
                        (SELECT COUNT(REVIEWS_LIKES.REVIEW_ID)
                         FROM REVIEWS_LIKES
                         WHERE IS_LIKE IS FALSE AND REVIEWS_LIKES.REVIEW_ID = REVIEWS.REVIEW_ID)) AS USEFUL
                FROM REVIEWS
                ORDER BY USEFUL desc
                LIMIT :count;
                """;

        Map<String, Object> param = Map.of("count", count);

        return jdbc.query(sql, param, new ReviewsExtractor());
    }

    @Override
    public List<Review> getReviewsByFilm(int filmId, int count) {
        //не уверен в получении полезности
        String sql = """
                SELECT REVIEW_ID,
                       CONTENT,
                       IS_POSITIVE,
                       USER_ID,
                       FILM_ID,
                       ((SELECT COUNT(REVIEWS_LIKES.REVIEW_ID)
                         FROM REVIEWS_LIKES
                         WHERE IS_LIKE IS TRUE AND REVIEWS_LIKES.REVIEW_ID = REVIEWS.REVIEW_ID) -
                        (SELECT COUNT(REVIEWS_LIKES.REVIEW_ID)
                         FROM REVIEWS_LIKES
                         WHERE IS_LIKE IS FALSE AND REVIEWS_LIKES.REVIEW_ID = REVIEWS.REVIEW_ID)) AS USEFUL
                FROM REVIEWS
                WHERE FILM_ID = :film_id
                ORDER BY USEFUL desc
                LIMIT :count;
                """;

        Map<String, Object> param = Map.of("film_id", filmId, "count", count);

        return jdbc.query(sql, param, new ReviewsExtractor());
    }
}
