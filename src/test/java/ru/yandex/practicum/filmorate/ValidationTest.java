package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.util.Util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testingValidationUserLogin() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User("log in", "qweqw@mail.com", LocalDate.now().minusYears(10)));

        assertEquals(1, violations.size());
    }

    @Test
    public void testingValidationUserEmail() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User("login", "email", LocalDate.now().minusYears(10)));

        assertEquals(1, violations.size());
    }

    @Test
    public void testingValidationUserBirthday() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User("login", "email@email.com", LocalDate.now().plusYears(10)));

        assertEquals(1, violations.size());
    }

    @Test
    public void testingValidationFilmName() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("", "description", LocalDate.now(), 100));

        assertEquals(1, violations.size());
    }

    @Test
    public void testingValidationFilmNameIsBlank() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film(" ", "description", LocalDate.now(), 100));

        assertEquals(1, violations.size());
    }

    @Test
    public void testingValidationFilmDescriptionIsEmpty() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("name", "", LocalDate.now(), 100));

        assertEquals(1, violations.size());
    }

    @Test
    public void testingValidationFilmDescriptionLengthMore200() {
        Set<ConstraintViolation<Film>> violations = validator.validate(
                new Film(
                        "name",
                        "descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescr" +
                                "iptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondes" +
                                "criptiondescriptiondescriptiondes", LocalDate.now(), 100));

        assertEquals(1, violations.size());
    }

    @Test
    public void testingValidationFilmDateRelease() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("name", "description", Util.VALIDATION_DATE.minusYears(1), 100));

        assertEquals(1, violations.size());
    }

    @Test
    public void testingValidationFilmDuration() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("name", "description", LocalDate.now(), 0));

        assertEquals(1, violations.size());
    }
}
