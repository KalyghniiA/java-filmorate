package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.EmptyBodyException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserException;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user) throws EmptyBodyException;

    User delete(Integer id) throws EmptyBodyException, NotFoundUserException;

    User put(User user) throws NotFoundUserException, EmptyBodyException;

    User get(Integer id) throws EmptyBodyException, NotFoundUserException;

    Collection<User> getAll();
}
