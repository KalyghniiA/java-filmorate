package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Map;

public interface ReviewUsefulRepository {
    Integer getUsefulToReview(int reviewId);

    Map<Integer, Integer> getUsefulToReviews(List<Integer> reviewsId);

    void addLikeToReview(int reviewId, int userId);

    void addDislikeToReview(int reviewId, int userId);

    void deleteLikeToReview(int reviewId, int userId);

    void deleteDislikeToReview(int reviewId, int userId);
}
