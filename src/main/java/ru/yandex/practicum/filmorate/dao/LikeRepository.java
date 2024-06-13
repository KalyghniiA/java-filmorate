package ru.yandex.practicum.filmorate.dao;


import java.util.List;

public interface LikeRepository {
    void addLike(int idFilm, int idUser);

    void removeLike(int idFilm, int idUser);

    List<Integer> getLikeByFilm(int filmId);


}
