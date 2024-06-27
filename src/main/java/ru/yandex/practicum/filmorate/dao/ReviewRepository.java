package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Optional<Review> getById(int id);

    Review save(Review review);

    void update(Review review);

    void delete(int id);

    List<Review> getReviews(int count);

    List<Review> getReviewsByFilm(int filmId, int count);
}
