package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    @NotBlank(message = "Пустое значение логина")
    @Pattern(regexp = "^\\w+$", message = "Логин не валиден")
    private final String login;
    private String name;
    @NotNull(message = "Пустое значение email")
    @Email(message = "Email не валиден")
    private final String email;
    @NotNull(message = "Пусто значение даты дня рождения")
    @Past(message = "День рождения не валиден")
    private final LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();
}
