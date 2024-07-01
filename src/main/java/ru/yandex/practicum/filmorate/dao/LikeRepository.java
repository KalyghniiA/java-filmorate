package ru.yandex.practicum.filmorate.dao;

public interface LikeRepository {
    void add(int filmId, int userId);

    void remove(int filmId, int userId);
}
