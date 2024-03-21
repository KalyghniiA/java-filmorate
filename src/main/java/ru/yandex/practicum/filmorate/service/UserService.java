package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    User get(int id);

    User post(User user);

    void delete(int id);

    User put(User user);

    Collection<User> getAll();

    User addFriend(int id, int friendId);

    void deleteFriend(int id, int friendid);

    Collection<User> getFriends(int id);

    Collection<User> getMutualFriends(int id, int otherId);
}
