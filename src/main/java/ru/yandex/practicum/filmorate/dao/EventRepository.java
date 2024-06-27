package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventRepository {
    void addEvent(Event event);

    List<Event> getUserEvents(Integer userId);
}
