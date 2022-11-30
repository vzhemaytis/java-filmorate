package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.validator.impl.ReleaseDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDateValid {

    String message() default "error release date data";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
