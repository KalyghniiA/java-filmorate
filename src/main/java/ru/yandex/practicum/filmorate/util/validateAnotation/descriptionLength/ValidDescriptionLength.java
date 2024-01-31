package ru.yandex.practicum.filmorate.util.validateAnotation.descriptionLength;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDescriptionLengthValidator.class)
public @interface ValidDescriptionLength {
    String message() default "Количество символов в описании не валидно";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
