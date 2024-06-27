package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewExtractor implements ResultSetExtractor<Review> {
    @Override
    public Review extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.next()) return null;
        Review review = new Review(
                rs.getString("CONTENT"),
                rs.getBoolean("IS_POSITIVE"),
                rs.getInt("USER_ID"),
                rs.getInt("FILM_ID"));
        review.setId(rs.getInt("REVIEW_ID"));

        return review;
    }
}
