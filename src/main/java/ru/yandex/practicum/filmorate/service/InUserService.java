package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class InUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public InUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User get(int id) {
        User user = userStorage.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
        return userStorage.get(id);
    }

    @Override
    public User post(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    @Override
    public void delete(int id) {
        User user = userStorage.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
        userStorage.delete(id);
    }

    @Override
    public User put(User user) {
        if (user.getId() == null) {
            throw new ValidationException("В теле отсутствует параметр id");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        User oldUser = userStorage.get(user.getId());
        if (oldUser == null) {
            throw new NotFoundException(String.format("Пользователя id %s нет в базе", user.getId()));
        }
        return userStorage.put(user);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User addFriend(int id, int friendId) {
        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
        if (friend == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId));
        }
        if (user.getFriends().containsKey(friendId)) {
            return user;
        }

        userStorage.addFriend(id, friendId);
        return user;
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }
        if (friend == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId));
        }
        if (!user.getFriends().containsKey(friendId)) {
            return;
        }


        userStorage.deleteFriend(id, friendId);
    }

    @Override
    public Collection<User> getFriends(int id) {
        User user = userStorage.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        }

        return userStorage.getFriends(id);
    }

    @Override
    public Collection<User> getMutualFriends(int id, int otherId) {
        User user = userStorage.get(id);
        User other = userStorage.get(otherId);

        if (user == null) {
            throw new NotFoundException(String.format("пользователя с id %s нет в базе", id));
        }
        if (other == null) {
            throw new NotFoundException(String.format("пользователя с id %s нет в базе", otherId));
        }

        return userStorage.getMutualFriends(id, otherId);
    }
}
