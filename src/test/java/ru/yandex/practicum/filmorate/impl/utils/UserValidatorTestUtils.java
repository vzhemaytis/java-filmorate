package ru.yandex.practicum.filmorate.impl.utils;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidatorTestUtils {

    public static User getUserDto(int id, String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }
}
