package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user);

    void delete(int id);

    User put(User user);

    User get(int id);

    Collection<User> getAll();

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    Collection<User> getFriends(int id);

    Collection<User> getMutualFriends(int id, int otherId);
}
