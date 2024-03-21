package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyBodyException;
import ru.yandex.practicum.filmorate.exception.EmptyDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserFriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public InUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User get(Integer id) {
        if (id == null) throw new EmptyDataException("Передано пустое значение");
        User user = userStorage.get(id);
        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        return userStorage.get(id);
    }

    @Override
    public User post(User user) {
        if (user == null) throw new EmptyBodyException("Передано пустое поле");
        return userStorage.add(user);
    }

    @Override
    public void delete(Integer id) {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        User user = userStorage.get(id);
        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        userStorage.delete(id);
    }

    @Override
    public User put(User user) {
        if (user == null) throw new EmptyBodyException("Передано пустое значение");
        User oldUser = userStorage.get(user.getId());
        if (oldUser == null) throw new NotFoundException(String.format("Пользователя id %s нет в базе", user.getId()));
        return userStorage.put(user);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User addFriend(Integer id, Integer friendId) {
        if (id == null || friendId == null) throw new EmptyBodyException("Передано пустое значение");

        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);

        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        if (friend == null)  throw new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId));
        if (user.getFriends().contains(friendId) && friend.getFriends().contains(id)) {
            throw new UserFriendException(String.format("Пользователи с id %s и %s уже друзья", id, friendId));
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        return user;
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        if (id == null || friendId == null) throw new EmptyBodyException("Передано пустое значение");

        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);

        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        if (friend == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId));
        if (!user.getFriends().contains(friendId) || !friend.getFriends().contains(id)) {
            return;
            //Проблема с тестами
            //throw new UserFriendException(String.format("Пользователи с id %s и %s не друзья", id, friendId));
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    @Override
    public Collection<User> getFriends(Integer id) {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");

        User user = userStorage.get(id);
        if (user == null) throw new NotFoundException(String.format("Пользователя с id %s нет в базе", id));
        Set<Integer> friendsId = user.getFriends();
        List<User> friends = new ArrayList<>();

        for (Integer friendId: friendsId) {
            User friend = userStorage.get(friendId);
            if (friend == null) {
               //прошу обратить внимание, требуется ли выбрасывать ошибку?
               log.debug(String.format("Друг c id %s удален из базы", friendId));
               continue;
            }
            friends.add(userStorage.get(friendId));
        }

        return friends;
    }

    @Override
    public Collection<User> getMutualFriends(Integer id, Integer otherId) {
        if (id == null || otherId == null) throw new EmptyBodyException("Передано пустое значение");

        User user = userStorage.get(id);
        User other = userStorage.get(otherId);

        if (user == null) throw new NotFoundException(String.format("пользователя с id %s нет в базе", id));
        if (other == null) throw new NotFoundException(String.format("пользователя с id %s нет в базе", otherId));

        List<Integer> mutualFriendsId = user.getFriends().stream()
                .filter(friendId -> other.getFriends().contains(friendId))
                .collect(Collectors.toList());
        List<User> mutualFriends = new ArrayList<>();
        for (Integer friendId: mutualFriendsId) {
            User friend = userStorage.get(friendId);
            if (friend == null) {
                //прошу обратить внимание, требуется ли выбрасывать ошибку
                log.debug(String.format("пользователя с id %s нет в базе", friendId));
                continue;
            }
            mutualFriends.add(friend);
        }

        return mutualFriends;
    }
}
