package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;
    private final EventService eventService;



    @Autowired
    public UserServiceImpl(UserRepository userStorage,  EventService eventService) {
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    @Override
    public User get(int id) {
        return userStorage.getById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Пользователя с id %s нет в базе", id))
                );
    }

    @Override
    public User post(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.save(user);
    }

    @Override
    public void delete(int id) {
        User user = userStorage
                .getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", id)));
        userStorage.delete(id);
    }

    @Override
    public User put(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.getById(user.getId()).orElseThrow(() -> new NotFoundException(String.format("Пользователя id %s нет в базе", user.getId())));
        userStorage.update(user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User addFriend(int id, int friendId) {
        User user = userStorage.getById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", id)));
        userStorage.getById(friendId).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId)));

        userStorage.addFriend(id, friendId);
        eventService.addEvent(new Event(id, EventType.FRIEND.name(), Operation.ADD.name(),
                friendId, System.currentTimeMillis()));
        return userStorage.getById(id).orElseThrow();
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        userStorage.getById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", id)));
        userStorage.getById(friendId).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId)));
        userStorage.deleteFriend(id, friendId);
        eventService.addEvent(new Event(id, EventType.FRIEND.name(), Operation.REMOVE.name(),
                friendId, System.currentTimeMillis()));
    }

    @Override
    public List<User> getFriends(int id) {
        userStorage.getById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", id)));
        return userStorage.getFriends(id);
    }

    @Override
    public List<User> getMutualFriends(int id, int otherId) {
        userStorage.getById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", id)));
        userStorage.getById(otherId).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", otherId)));

        return userStorage.getMutualFriends(id, otherId);
    }

    @Override
    public List<Event> getEventsByUser(Integer userId) {
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", userId)));
        List<Event> events = eventService.getUserEvents(userId);
        if (events.isEmpty()) {
            throw new NotFoundException("У данного пользователя нет событий");
        }

        return events;
    }
}
