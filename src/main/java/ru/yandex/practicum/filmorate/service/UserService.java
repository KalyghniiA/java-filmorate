package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    User get(Integer id);

    User post(User user);

    void delete(Integer id);

    User put(User user);

    Collection<User> getAll();

    User addFriend(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendid);

    Collection<User> getFriends(Integer id);

    Collection<User> getMutualFriends(Integer id, Integer otherId);
}
