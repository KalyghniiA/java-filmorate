package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserRepository {
    User save(User user);

    void delete(int userId);

    User update(User user);

    User getById(int userId);

    Collection<User> getAll();

    User addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Collection<User> getFriends(int userId);

    Collection<User> getMutualFriends(int userId, int otherId);

    boolean checkIsFriends(int userId, int otherId);
}
