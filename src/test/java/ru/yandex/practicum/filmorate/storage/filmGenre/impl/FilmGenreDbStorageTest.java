package ru.yandex.practicum.filmorate.storage.filmGenre.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorageTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGenreDbStorageTest extends FilmGenreStorageTest<FilmGenresDbStorage> {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;

    @AfterEach
    void deleteData() {
        jdbcTemplate.update("drop all objects");
        jdbcTemplate.update("runscript from 'src/test/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/data.sql'");
    }

    @Test
    @DisplayName("1) Проверка получения пустого списка жанров фильма")
    void addFilmGenreTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        Set<Genre> genres = filmGenreStorage.getFilmGenres(7L);
        assertTrue(genres.isEmpty());
    }

    @Test
    @DisplayName("2) Проверка добавления и получения жанров фильма")
    void addFilmGenreTest2() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        filmGenreStorage.addFilmGenre(7L, Set.of(new Genre(1)));
        Set<Genre> genres = filmGenreStorage.getFilmGenres(7L);
        assertAll(
                () -> assertEquals(1, genres.size()),
                () -> assertTrue(genres.contains(new Genre(1, "Комедия")))
        );
        filmGenreStorage.addFilmGenre(7L, Set.of(new Genre(1), new Genre(2)));
        Set<Genre> twoGenres = filmGenreStorage.getFilmGenres(7L);
        assertAll(
                () -> assertEquals(2, twoGenres.size()),
                () -> assertTrue(twoGenres.contains(new Genre(1, "Комедия"))),
                () -> assertTrue(twoGenres.contains(new Genre(2, "Драма")))
        );
    }

    @Test
    @DisplayName("3) Проверка обновления жанров фильма")
    void addFilmGenreTest3() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        filmGenreStorage.addFilmGenre(7L, Set.of(new Genre(1)));
        Set<Genre> genres = filmGenreStorage.getFilmGenres(7L);
        assertAll(
                () -> assertEquals(1, genres.size()),
                () -> assertTrue(genres.contains(new Genre(1, "Комедия")))
        );
        filmGenreStorage.addFilmGenre(7L, Set.of(new Genre(2)));
        Set<Genre> newGenres = filmGenreStorage.getFilmGenres(7L);
        assertAll(
                () -> assertEquals(1, newGenres.size()),
                () -> assertFalse(newGenres.contains(new Genre(1, "Комедия"))),
                () -> assertTrue(newGenres.contains(new Genre(2, "Драма")))
        );
    }

    @Test
    @DisplayName("4) Получение ошибки при добавлении несуществующего жанра")
    void addFilmGenreTest4() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () ->         filmGenreStorage.addFilmGenre(7L, Set.of(new Genre(7)))
        );
    }
}
