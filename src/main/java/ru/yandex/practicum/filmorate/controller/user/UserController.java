package ru.yandex.practicum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserControllerException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private Integer nextId = 1;

    @PostMapping(value = "/users", headers = "content-type=application/json")
    public User postUser(@RequestBody @Valid User user) throws UserControllerException, ValidationException {
        if (user == null) {
            log.info("Передано пустое значение");
            throw new UserControllerException("Передано пустое значение");
        }
        if (user.getId() != null && users.containsKey(user.getId())) {
            log.info("Данный юзер уже добавлен в базу");
            throw new UserControllerException("Данный юзер уже добавлен в базу");
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
    public User getUser(@PathVariable("id") String id) throws UserControllerException {
        Integer idKey;
        try {
            idKey = Integer.valueOf(id);
        } catch (NumberFormatException e) {
            log.info("Не верный формат id");
            throw new UserControllerException(e.getMessage());
        }
        if (!users.containsKey(idKey)) {
            log.info("Данного юзера нет в базе по id" + id);
            throw new UserControllerException("Данного юзера нет в базе");
        }
        User user = users.get(idKey);
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

        users.put(user.getId(), user);
        log.info("Изменен пользователь под id" + user.getId());
        return user;
    }
}
//    private boolean isValid(User user) {
//        if (user == null) {
//            return false;
//        }
//        String email = user.getEmail();
//        String login = user.getLogin();
//        String name = user.getName();
//        LocalDate date = user.getBirthday();
//
//        if (email == null ||
//            login == null ||
//            name == null ||
//            date == null) {
//            log.info("Один или несколько параметров имеет значение null");
//            return false;
//        }
//
//        if (email.isEmpty() || email.isBlank() || !email.contains("@")) {
//            log.info("Email не валиден");
//            return false;
//        }
//
//        if (!login.matches("^\\w+$")) {
//            log.info("Логин не валиден");
//            return false;
//        }
//
//        if (date.isAfter(LocalDate.now())) {
//            log.info("День рождения не валиден");
//            return false;
//        }
//
//        return true;
//    }

//    Для UserController:
//        создание пользователя;
//        обновление пользователя;
//        получение списка всех пользователей.