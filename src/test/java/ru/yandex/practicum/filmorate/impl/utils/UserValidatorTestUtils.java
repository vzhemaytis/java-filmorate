package ru.yandex.practicum.filmorate.impl.utils;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidatorTestUtils {

    public static User getUserDto(Long id, String email, String login, String name, LocalDate birthday) {
        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }
}
