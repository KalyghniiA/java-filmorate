package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Map;

public interface ReviewUsefulRepository {
    Integer getUseful(int reviewId);

    Map<Integer, Integer> getUseful(List<Integer> reviewsId);

    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);
}
