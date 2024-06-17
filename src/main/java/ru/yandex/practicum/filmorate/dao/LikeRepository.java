package ru.yandex.practicum.filmorate.dao;

public interface LikeRepository {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);
}
