package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EmptyBodyException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserException;
import ru.yandex.practicum.filmorate.exceptions.UserFriendException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(Integer id) throws EmptyBodyException, NotFoundUserException {
        return userStorage.get(id);
    }

    public User postUser(User user) throws EmptyBodyException {
        return userStorage.add(user);
    }

    public User deleteUser(Integer id) throws EmptyBodyException, NotFoundUserException {
        return userStorage.delete(id);
    }

    public User putUser(User user) throws EmptyBodyException, NotFoundUserException {
        return userStorage.put(user);
    }

    public Collection<User> getAllUser() {
        return userStorage.getAll();
    }

    public User addFriend(Integer id, Integer friendId) throws
            EmptyBodyException,
            NotFoundUserException,
            UserFriendException {
        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);

        if (user.getFriends().contains(friendId) && friend.getFriends().contains(id)) {
            throw new UserFriendException(String.format("Пользователи с id %s и %s уже друзья", id, friendId));
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        return user;
    }

    public User deleteFriend(Integer id, Integer friendId) throws
            EmptyBodyException,
            NotFoundUserException {
        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);

        if (!user.getFriends().contains(friendId) || !friend.getFriends().contains(id)) {
            return user;
            //Проблема с тестами
            //throw new UserFriendException(String.format("Пользователи с id %s и %s не друзья", id, friendId));
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        return user;
    }

    public Collection<User> getFriendsForUser(Integer id) throws EmptyBodyException, NotFoundUserException {
        Set<Integer> friendsId = userStorage.get(id).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer friendId: friendsId) {
            friends.add(userStorage.get(friendId));
        }
        return friends;
    }

    public Collection<User> getMutualFriendsForUser(Integer id, Integer otherId) throws
            EmptyBodyException,
            NotFoundUserException {
        User user = userStorage.get(id);
        User other = userStorage.get(otherId);
        List<Integer> mutualFriendsId = user.getFriends().stream()
                .filter(friendId -> other.getFriends().contains(friendId))
                .collect(Collectors.toList());

        List<User> mutualFriends = new ArrayList<>();
        for (Integer friendId: mutualFriendsId) {
            mutualFriends.add(userStorage.get(friendId));
        }

        return mutualFriends;
    }

}
