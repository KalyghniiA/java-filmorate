package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewsExtractor implements ResultSetExtractor<List<Review>> {
    @Override
    public List<Review> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Review> reviews = new ArrayList<>();

        while (rs.next()) {
            Review review = new Review(
                    rs.getString("CONTENT"),
                    rs.getBoolean("IS_POSITIVE"),
                    rs.getInt("USER_ID"),
                    rs.getInt("FILM_ID")
            );
            review.setId(rs.getInt("REVIEW_ID"));
            review.setUseful(rs.getInt("USEFUL"));
            reviews.add(review);
        }

        return reviews;
    }
}
