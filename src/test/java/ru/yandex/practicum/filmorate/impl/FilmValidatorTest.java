package ru.yandex.practicum.filmorate.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.impl.utils.FilmValidatorTestUtils.getFilmDto;
import static ru.yandex.practicum.filmorate.utils.ValidatorTestUtils.dtoHasErrorMessage;

public class FilmValidatorTest {

    private final LocalDate releaseDate = LocalDate.of(2021, 12, 12);

    @Test
    @DisplayName("1) Проверка невалидности названия в dto со значением null")
    public void createFilmNotValidNullNameTest() {
        Film film = getFilmDto(1L, null, "desc" , releaseDate, 100);
        assertTrue(dtoHasErrorMessage(film, "name should be not blank"));
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности названия \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    @DisplayName("2) Проверка невалидности названия в dto с пустыми значениями")
    public void createFilmNotValidBlankNameTest(String name) {
        Film film = getFilmDto(1L, name, "desc" , releaseDate, 100);
        assertTrue(dtoHasErrorMessage(film, "name should be not blank"));
    }

    @Test
    @DisplayName("3) Проверка невалидности описания в dto с значение больше 200 знаков")
    public void createFilmNotValidTooLongDescriptionTest() {
        String description = "1".repeat(201);
        Film film = getFilmDto(1L, "name", description , releaseDate, 100);
        assertTrue(dtoHasErrorMessage(film, "description should be shorter 200 letters"));
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности длительности \"{arguments}\"")
    @ValueSource(ints = {Integer.MIN_VALUE, -10, -5, -1, 0})
    @DisplayName("4) Проверка невалидности длительности в dto")
    public void createFilmNotValidNotPositiveDurationTest(int duration) {
        Film film = getFilmDto(1L, "name", "description", releaseDate, duration);
        Assertions.assertTrue(dtoHasErrorMessage(film, "duration should be positive"));
    }

    @Test
    @DisplayName("5) Проверка невалидности даты релиза в dto со значением раньше 28.12.1895")
    public void createFilmNotValidTooOldReleaseDateTest() {
        LocalDate wrongReleaseDate = LocalDate.of(1895, 12,27);
        Film film = getFilmDto(1L, "name", "description" , wrongReleaseDate, 100);
        assertTrue(dtoHasErrorMessage(film, "release date should be past 28.12.1895"));
    }
}
