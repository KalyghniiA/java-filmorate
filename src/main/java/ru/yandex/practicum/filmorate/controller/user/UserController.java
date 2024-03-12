package ru.yandex.practicum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users", headers = "content-type=application/json")
    public User postUser(@RequestBody @Valid User user) throws EmptyBodyException {
        return userService.postUser(user);
    }

    @GetMapping(value = "/users/{id}")
    public User getUser(@PathVariable("id") Integer id) throws EmptyBodyException, NotFoundUserException {
        return userService.getUser(id);
    }

    @GetMapping(value = "/users")
    public Collection<User> getUsers() {
        return userService.getAllUser();
    }

    @PutMapping(value = "/users", headers = "content-type=application/json")
    public User putUser(@RequestBody @Valid User user) throws EmptyBodyException, NotFoundUserException {
        return userService.putUser(user);
    }

    @DeleteMapping(value = "/users/{id}")
    public User deleteUser(@PathVariable Integer id) throws EmptyBodyException, NotFoundUserException {
        return userService.deleteUser(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public User addFriendForUser(@PathVariable Integer id, @PathVariable Integer friendId) throws
            EmptyBodyException,
            UserFriendException,
            NotFoundUserException {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public User deleteFriendForUser(@PathVariable Integer id, @PathVariable Integer friendId) throws
            EmptyBodyException,
            NotFoundUserException {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/users/{id}/friends")
    public Collection<User> getFriendsForUser(@PathVariable Integer id) throws
            EmptyBodyException,
            NotFoundUserException {
        return userService.getFriendsForUser(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriendsForUser(@PathVariable Integer id, @PathVariable Integer otherId) throws
            EmptyBodyException,
            NotFoundUserException {
        return userService.getMutualFriendsForUser(id, otherId);
    }
}
