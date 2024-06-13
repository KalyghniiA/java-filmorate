package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Service
@Slf4j
public class ImplUserService implements UserService {
    private final UserRepository userStorage;

    @Autowired
    public ImplUserService(UserRepository userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User get(int id) {
        try {
            return userStorage.getById(id).orElseThrow();
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
    }

    @Override
    public User post(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.save(user).orElseThrow();
    }

    @Override
    public void delete(int id) {
        User user = userStorage.getById(id).orElseThrow();
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
        userStorage.delete(id);
    }

    @Override
    public User put(User user) {
        if (user.getId() == null) {
            post(user);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        try {
            userStorage.getById(user.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя id %s нет в базе", user.getId()));
        }
        return userStorage.update(user).orElseThrow();
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User addFriend(int id, int friendId) {
        User user;
        try {
            user = userStorage.getById(id).orElseThrow();
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
        try {
            userStorage.getById(friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId));
        }

        if (user.getFriends().contains(friendId)) {
            return user;
        }

        userStorage.addFriend(id, friendId);
        return userStorage.getById(id).orElseThrow();
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        try {
            userStorage.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
        try {
            userStorage.getById(friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId));
        }

        if (!userStorage.checkIsFriends(id, friendId)) {
            return;
        }

        userStorage.deleteFriend(id, friendId);
    }

    @Override
    public Collection<User> getFriends(int id) {
        try {
            userStorage.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
        return userStorage.getFriends(id);
    }

    @Override
    public Collection<User> getMutualFriends(int id, int otherId) {
        try {
            userStorage.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("пользователя с id %s нет в базе", id));
        }
        try {
            userStorage.getById(otherId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("пользователя с id %s нет в базе", otherId));
        }

        return userStorage.getMutualFriends(id, otherId);
    }
}
