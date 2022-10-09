package ru.yandex.practicum.filmorate.validator.impl;

import ru.yandex.practicum.filmorate.validator.LoginValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<LoginValid, String> {
    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        return login != null && !login.contains(" ");
    }
}
