package ru.yandex.practicum.filmorate.validator.impl;

import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Дата релиа фильма должна быть не раньше 28 декабря 1895 года
 */
public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValid, LocalDate> {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return !releaseDate.isBefore(MIN_RELEASE_DATE);
    }
}
