package ru.yandex.practicum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserControllerException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer nextId = 1;

    @PostMapping(value = "/users", headers = "content-type=application/json")
    public User postUser(@RequestBody @Valid User user) throws UserControllerException, ValidationException {
        if (user == null) {
            log.info("Передано пустое значение");
            throw new UserControllerException("Передано пустое значение");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь под id" + user.getId());
        return user;
    }

    @GetMapping(value = "/users/{id}")
    public User getUser(@PathVariable("id") Integer id) throws UserControllerException {

        if (!users.containsKey(id)) {
            log.info("Данного юзера нет в базе по id" + id);
            throw new UserControllerException("Данного юзера нет в базе");
        }
        User user = users.get(id);
        if (user == null) {
            log.info("В базе произошла ошибка, пустой объект в базе");
            throw new UserControllerException("В базе произошла ошибка, проверьте верность id");
        }
        return user;
    }

    @GetMapping(value = "/users")
    public Collection<User> getUsers() {
        return users.values();
    }


    @PutMapping(value = "/users", headers = "content-type=application/json")
    public User putUser(@RequestBody @Valid User user) throws UserControllerException, ValidationException {
        if (user == null) {
            log.info("Отправлено пустое значение");
            throw new UserControllerException("Отправлено пустое значение");
        }

        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.info("Данного юзера нет в базе по id" + user.getId());
            throw new UserControllerException("Данного юзера нет в базе");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Изменен пользователь под id" + user.getId());
        return user;
    }
}
