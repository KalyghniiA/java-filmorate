package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface LikeRepository {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Integer> getLikedFilmsByUserId(int userId);
}
