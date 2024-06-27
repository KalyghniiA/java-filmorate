package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class EventType {
    private final int id;
    private String name;
}
