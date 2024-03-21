package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @Override
    public User add(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    @Override
    public User put(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(int id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void addFriend(int id, int friendId) {
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
    }

    @Override
    public Collection<User> getFriends(int id) {
        Set<Integer> friendsId = users.get(id).getFriends();
        List<User> friends = new ArrayList<>();

        for (int friendId: friendsId) {
            friends.add(users.get(friendId));
        }

        return friends;
    }

    @Override
    public Collection<User> getMutualFriends(int id, int otherId) {
        User user = users.get(id);
        User other = users.get(otherId);
        List<Integer> mutualFriendsId = user.getFriends().stream()
                .filter(friendId -> other.getFriends().contains(friendId))
                .collect(Collectors.toList());
        List<User> mutualFriends = new ArrayList<>();
        for (int friendId: mutualFriendsId) {
            mutualFriends.add(users.get(friendId));
        }

        return mutualFriends;
    }
}
