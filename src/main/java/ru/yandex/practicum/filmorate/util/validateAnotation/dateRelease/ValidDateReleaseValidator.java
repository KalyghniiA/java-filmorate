package ru.yandex.practicum.filmorate.util.validateAnotation.dateRelease;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ValidDateReleaseValidator implements ConstraintValidator<ValidDateRelease, LocalDate> {
    private static final LocalDate VALIDATION_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null)
            return false;

        return date.isAfter(VALIDATION_DATE);
    }
}
