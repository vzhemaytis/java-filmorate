package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.validators.UserValidator.isUserValid;

class UserValidatorTest {
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("a@b.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990,12,12));
    }

    @Test
    void validationCheck() {
        assertTrue(isUserValid(user));
    }

    @Test
    void nullTest() {
        user = null;
        assertFalse(isUserValid(user));
    }

    @Test
    void hasNotEtEmailTest() {
        user.setEmail("ab.com");
        assertFalse(isUserValid(user));
    }

    @Test
    void nullEmailTest() {
        user.setEmail(null);
        assertFalse(isUserValid(user));
    }

    @Test
    void emptyEmailTest() {
        user.setEmail("");
        assertFalse(isUserValid(user));
    }

    @Test
    void nullLoginTest() {
        user.setLogin(null);
        assertFalse(isUserValid(user));
    }

    @Test
    void emptyLoginTest() {
        user.setLogin("");
        assertFalse(isUserValid(user));
    }

    @Test
    void hasSpaceLoginTest() {
        user.setLogin("Na me");
        assertFalse(isUserValid(user));
    }

    @Test
    void nullNameTest() {
        user.setName(null);
        isUserValid(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void emptyNameTest() {
        user.setName("");
        isUserValid(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void futureBirthdayTest() {
        user.setBirthday(LocalDate.of(2023,12,12));
        assertFalse(isUserValid(user));
    }

}