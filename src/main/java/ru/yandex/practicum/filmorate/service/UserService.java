package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User get(int id);

    User post(User user);

    void delete(int id);

    User put(User user);

    List<User> getAll();

    User addFriend(int id, int friendId);

    void deleteFriend(int id, int friendid);

    List<User> getFriends(int id);

    List<User> getMutualFriends(int id, int otherId);

    List<Event> getEventsByUser(Integer userId);
}
