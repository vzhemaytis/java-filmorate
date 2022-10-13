package ru.yandex.practicum.filmorate.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.impl.utils.UserValidatorTestUtils.getUserDto;
import static ru.yandex.practicum.filmorate.utils.ValidatorTestUtils.dtoHasErrorMessage;

public class UserValidatorTest {
    private final LocalDate birthday = LocalDate.of(2000, 12, 12);

    @Test
    @DisplayName("1) Проверка невалидности логина и email в dto со значением null")
    public void createFilmTest() {
        User user = getUserDto(1, null, null, "name", birthday);
        assertAll(
                () -> assertTrue(dtoHasErrorMessage(user, "email should not be blank")),
                () -> assertTrue(dtoHasErrorMessage(user, "login should be not blank"))
        );
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности email \"{arguments}\"")
    @ValueSource(strings = {"@b.com", "ab.com", "qwerty@", "qwerty@b."})
    @DisplayName("2) Проверка невалидности email в dto с неправильным форматом email")
    public void createFilmTest2(String email) {
        User user = getUserDto(1, email, "login", "name", birthday);
        assertTrue(dtoHasErrorMessage(user, "wrong email pattern"));
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности логина \"{arguments}\"")
    @ValueSource(strings = {" ", "a b", " a", "a ", "a b "})
    @DisplayName("3) Проверка невалидности логина в dto с содержанием пробелов")
    public void createFilmTest3(String login) {
        User user = getUserDto(1, "a@b.com", login, "name", birthday);
        assertTrue(dtoHasErrorMessage(user, "login should not have spaces"));
    }

    @Test
    @DisplayName("4) Проверка невалидности дня рождения в dto со значением в будущем")
    public void createFilmTest4() {
        LocalDate futureBirthday = LocalDate.now().plusDays(1);
        User user = getUserDto(1, "a@b.com", "login", "name", futureBirthday);
        assertTrue(dtoHasErrorMessage(user, "birthday should be in the past"));
    }

}
