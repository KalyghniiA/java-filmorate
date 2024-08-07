package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Event {
    private Integer eventId; //primary key

    @NotNull
    private final Integer userId; // юзер, который совершил действие

    @NotBlank
    private final String eventType; // одно из значениий LIKE, REVIEW или FRIEND - тип события

    @NotBlank
    private final String operation; // одно из значениий REMOVE, ADD, UPDATE - тип операции

    @NotNull
    private final Integer entityId; // идентификатор сущности, с которой произошло событие

    @NotNull
    private Long timestamp =  System.currentTimeMillis();
}
