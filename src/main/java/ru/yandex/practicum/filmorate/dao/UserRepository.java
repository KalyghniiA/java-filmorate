package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> save(User user);

    void delete(int userId);

    Optional<User> update(User user);

    Optional<User> getById(int userId);

    List<User> getAll();

    User addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getMutualFriends(int userId, int otherId);

    boolean checkIsFriends(int userId, int otherId);
}
