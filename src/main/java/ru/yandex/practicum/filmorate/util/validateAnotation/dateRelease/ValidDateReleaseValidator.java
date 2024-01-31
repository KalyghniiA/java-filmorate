package ru.yandex.practicum.filmorate.util.validateAnotation.dateRelease;

import ru.yandex.practicum.filmorate.util.Util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ValidDateReleaseValidator implements ConstraintValidator<ValidDateRelease, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null)
            return false;

        return date.isAfter(Util.VALIDATION_DATE);
    }
}
