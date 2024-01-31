package ru.yandex.practicum.filmorate.util.validateAnotation.dateRelease;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDateReleaseValidator.class)
public @interface ValidDateRelease {
    String message() default "Передано не валидная дата";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
