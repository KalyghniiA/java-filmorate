package ru.yandex.practicum.filmorate.model.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

/*
* целочисленный идентификатор — id;
электронная почта — email;
логин пользователя — login;
имя для отображения — name;
дата рождения — birthday.*/
@Data
public class User {
    private Integer id;
    @NonNull
    @NotNull(message = "Пустое значение логина")
    @Pattern(regexp = "^\\w+$", message = "Логин не валиден")
    private final String login;
    private String name;
    @NonNull
    @NotNull(message = "Пустое значение email")
    @Email(message = "Email не валиден")
    private final String email;
    @NonNull
    @NotNull(message = "Пусто значение даты дня рождения")
    @Past(message = "День рождения не валиден")
    private final LocalDate birthday;
}
