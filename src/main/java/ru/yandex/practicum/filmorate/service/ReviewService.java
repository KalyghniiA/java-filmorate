package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review saveReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int reviewId);

    Review getReview(Integer reviewId);

    List<Review> getReviews(Integer filmId, Integer count);

    void addMarkToReview(int reviewId, int userId, boolean isLike);

    void deleteMarkToReview(int reviewId, int userId, boolean isLike);
}
