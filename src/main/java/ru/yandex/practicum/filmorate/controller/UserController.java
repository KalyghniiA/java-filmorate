package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InUserService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(InUserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User postUser(@RequestBody @Valid User user) {
        log.info("Получен POST запрос на добавление пользователя");
        User newUser = userService.post(user);
        log.info(String.format("Пользователь добавлен с id %s", newUser.getId()));
        return newUser;
    }

    @GetMapping(value = "/users/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        log.info("Получен GET запрос на получение пользователя");
        User user = userService.get(id);
        log.info(String.format("Пользователь с id %s отправлен", id));
        return user;
    }

    @GetMapping(value = "/users")
    public Collection<User> getUsers() {
        log.info("Получен GET запрос на получение всех пользователей");
        Collection<User> users = userService.getAll();
        log.info("Отправлены все пользователи");
        return users;
    }

    @PutMapping(value = "/users", headers = "content-type=application/json")
    public User putUser(@RequestBody @Valid User user) {
        log.info("Получен PUT запрос на изменение пользователя");
        User newUser = userService.put(user);
        log.info(String.format("Пользователь с id %s изменен", newUser.getId()));
        return newUser;
    }

    @DeleteMapping(value = "/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        log.info("Получен DELETE на удаление пользователя");
        userService.delete(id);
        log.info(String.format("Пользователь с id %s удален", id));
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public User addFriendForUser(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен PUT запрос на добавление друга пользователю");
        User user = userService.addFriend(id, friendId);
        log.info(String.format("Пользователю с id %s добавлен друг с id %s", id, friendId));
        return user;
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriendForUser(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен DELETE запрос на удаление друга у пользователя");
        userService.deleteFriend(id, friendId);
        log.info(String.format("Друг с id %s удален у пользователя с id %s", friendId, id));
    }

    @GetMapping(value = "/users/{id}/friends")
    public Collection<User> getFriendsForUser(@PathVariable Integer id) {
        log.info("Получен GET запрос на получение списка друзей пользователя");
        Collection<User> friends = userService.getFriends(id);
        log.info(String.format("Друзья пользователя с id %s отправлены",id));
        return friends;
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriendsForUser(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Получен GET запрос на получение общего списка друзей");
        Collection<User> friends = userService.getMutualFriends(id, otherId);
        log.info(String.format("Список общих друзей пользователей с id %s и %s отправлен", id, otherId));
        return friends;
    }
}
