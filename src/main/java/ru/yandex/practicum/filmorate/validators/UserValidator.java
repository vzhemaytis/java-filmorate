package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    private static final String SPACE = " ";
    private static final String ET = "@";

    public static boolean isUserValid(User user) {
        if (user == null) { // NotNull check
            return false;
        }
        if ( user.getEmail() == null || !user.getEmail().contains(ET) || user.getEmail().isEmpty()) { // Email check
            return false;
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(SPACE)) { // Login check
            return false;
        }
        if (user.getName() == null || user.getName().isEmpty()) { // Name check
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) { // Birthday check
            return false;
        }
        return true;
    }
}
