package ru.yandex.practicum.filmorate.model.user;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

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
}
