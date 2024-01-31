package ru.yandex.practicum.filmorate.util.validateAnotation.descriptionLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidDescriptionLengthValidator implements ConstraintValidator<ValidDescriptionLength, String> {
    @Override
    public boolean isValid(String description, ConstraintValidatorContext context) {
        if (description == null)
            return false;

        return !description.isEmpty() && description.length() <= 200;
    }
}
