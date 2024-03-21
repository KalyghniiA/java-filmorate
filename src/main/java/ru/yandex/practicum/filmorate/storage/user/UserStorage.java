package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user);

    User delete(Integer id);

    User put(User user);

    User get(Integer id);

    Collection<User> getAll();
}
