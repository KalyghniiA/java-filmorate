package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;
    private final FilmRepository filmRepository;
    private final EventService eventService;

    @Autowired
    public UserServiceImpl(UserRepository userStorage, FilmRepository filmRepository, EventService eventService) {
        this.userStorage = userStorage;
        this.filmRepository = filmRepository;
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
        if (user.getName() == null) {
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
        if (user.getName() == null) {
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
        eventService.addEvent(new Event(id, new EventType(3,"FRIEND"),new Operation(2, "ADD"),
                friendId, LocalDateTime.now()));
        return userStorage.getById(id).orElseThrow();
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        userStorage.getById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", id)));
        userStorage.getById(friendId).orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", friendId)));
        userStorage.deleteFriend(id, friendId);
        eventService.addEvent(new Event(id, new EventType(3,"FRIEND"),new Operation(1, "REMOVE"),
                friendId, LocalDateTime.now()));
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
    public List<Optional<Film>> getRecommendations(int userId) {
        List<Integer> userFilms = filmRepository.getLikedFilmsByUserId(userId);
        List<User> users = getAll();
        HashMap<Integer, List<Integer>> likes = new HashMap<>();
        for (User user : users) {
            if (user.getId() != userId) {
                likes.put(user.getId(), filmRepository.getLikedFilmsByUserId(user.getId()));
            }
        }
        int maxCommonElementsCount = 0;
        List<Integer> films = new ArrayList<>();
        for (Integer anotherUserId : likes.keySet()) {
            List<Integer> likedFilms = likes.get(anotherUserId);
            int commonSum = 0;
            for (Integer filmId : userFilms) {
                for (Integer anotherFilmId : likedFilms) {
                    if (filmId == anotherFilmId) {
                        commonSum++;
                    }
                }
            }
            if (commonSum > maxCommonElementsCount) {
                maxCommonElementsCount = commonSum;
                films = likedFilms;
            }
        }
        films.removeAll(userFilms);
        List<Optional<Film>> recommendations = new ArrayList<>();
        for (Integer filmId : films) {
            recommendations.add(filmRepository.getById(filmId));
        }
        log.info(String.format("Список рекомендаций для пользователя с id %s отправлен", userId));
        return recommendations;
    }

    @Override
    public List<Event> getEventsByUser(Integer userId) {
        return eventService.getUserEvents(userId);
    }
}
