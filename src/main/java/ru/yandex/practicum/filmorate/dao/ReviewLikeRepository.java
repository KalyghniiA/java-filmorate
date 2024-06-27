package ru.yandex.practicum.filmorate.dao;

public interface ReviewLikeRepository {
    void addLikeForReview(int reviewId, int userId);

    void addDislikeForReview(int reviewId, int userId);

    void deleteLikeForReview(int reviewId, int userId);

    void deleteDislikeForReview(int reviewId, int userId);
}
