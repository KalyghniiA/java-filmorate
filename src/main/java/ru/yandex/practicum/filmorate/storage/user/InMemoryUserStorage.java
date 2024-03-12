package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EmptyBodyException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserException;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @Override
    public User add(User user) throws EmptyBodyException {
        if (user == null) throw new EmptyBodyException("Передано пустое поле");
        if (user.getName() == null) user.setName(user.getLogin());

        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Integer id) throws EmptyBodyException, NotFoundUserException {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        if (!users.containsKey(id)) throw new NotFoundUserException(String.format("User с id %s нет в базе", id));

        return users.remove(id);
    }

    @Override
    public User put(User user) throws NotFoundUserException, EmptyBodyException {
        if (user == null) throw new EmptyBodyException("Передано пустое значение");
        if (!users.containsKey(user.getId())) {
            throw new NotFoundUserException(String.format("User с id %s нет в базе", user.getId()));
        }
        if (user.getName() == null) user.setName(user.getLogin());

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Integer id) throws EmptyBodyException, NotFoundUserException {
        if (id == null) throw new EmptyBodyException("Передано пустое значение");
        if (!users.containsKey(id)) throw new NotFoundUserException(String.format("User с id %s нет в базе", id));
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }
}
