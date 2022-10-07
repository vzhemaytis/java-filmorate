package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.validators.FilmValidator.isFilmValid;

class FilmValidatorTest {
    private Film film;

    @BeforeEach
    void setup() {
        film = new Film();
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1999,12,12));
        film.setDuration(100);
    }


    @Test
    void validationTest() {
        assertTrue(isFilmValid(film));
    }

    @Test
    void nullTest() {
        film = null;
        assertFalse(isFilmValid(film));
    }

    @Test
    void nullNameTest() {
        film.setName(null);
        assertFalse(isFilmValid(film));
    }

    @Test
    void emptyNameTest() {
        film.setName("");
        assertFalse(isFilmValid(film));
    }

    @Test
    void maxSizeDescTest() {
        film.setDescription("1".repeat(200));
        assertTrue(isFilmValid(film));
    }

    @Test
    void emptyDescTest() {
        film.setDescription("");
        assertTrue(isFilmValid(film));
    }

    @Test
    void tooLongDescTest() {
        film.setDescription("1".repeat(201));
        assertFalse(isFilmValid(film));
    }

    @Test
    void minReleaseDateTest() {
        film.setReleaseDate(LocalDate.of(1895,12,28));
        assertTrue(isFilmValid(film));
    }

    @Test
    void beforeMinReleaseDateTest() {
        film.setReleaseDate(LocalDate.of(1895,12,27));
        assertFalse(isFilmValid(film));
    }

    @Test
    void zeroDurationTest() {
        film.setDuration(0);
        assertFalse(isFilmValid(film));
    }

    @Test
    void negativeDurationTest() {
        film.setDuration(-1);
        assertFalse(isFilmValid(film));
    }

}