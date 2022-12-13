package ru.yandex.practicum.filmorate.storage.director.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorageTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbStorageTest extends DirectorStorageTest<DirectorDbStorage> {

    private final JdbcTemplate jdbcTemplate;

    private final DirectorStorage directorStorage;

    @AfterEach
    void deleteData() {
        jdbcTemplate.update("drop all objects");
        jdbcTemplate.update("runscript from 'src/test/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/data.sql'");
    }

    @Test
    @DisplayName("1) Получение всех режиссеров")
    void getDirectorsEmptyTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertEquals(6, directorStorage.getDirectors().size());
    }

    @Test
    @DisplayName("2) Получение пустого списка режиссеров")
    void getDirectorsTest() {
        assertTrue(directorStorage.getDirectors().isEmpty());
    }

    @Test
    @DisplayName("3) Добавление нового режиссера")
    void addDirectorTest() {
        Director director = new Director(10L, "A");
        directorStorage.addDirector(director);

        assertAll(
                () -> assertEquals(director, directorStorage.getDirector(1L).orElse(null)),
                () -> assertEquals(1, directorStorage.getDirectors().size())
        );
    }

    @Test
    @DisplayName("4) Обновление режиссера")
    void directorUpdateTest() {
        Director director = new Director(10L, "A");
        directorStorage.addDirector(director);
        assertAll(
                () -> assertEquals(director, directorStorage.getDirector(1L).orElse(null)),
                () -> assertEquals(1, directorStorage.getDirectors().size())
        );

        directorStorage.updateDirector(new Director(1L, "B"));

        assertEquals("B", Objects.requireNonNull(directorStorage.getDirector(1L).orElse(null)).getName());
    }

    @Test
    @DisplayName("5) Обновление не существующего режиссера")
    void updateDirectorThatDoesntExistsTest() {
        Director director = new Director(1L, "A");
        assertThrows(
                EntityNotFoundException.class,
                () -> directorStorage.updateDirector(director)
        );
    }

    @Test
    @DisplayName("6) Получение режиссера по несуществующему ID")
    void getDirectorWithWrongIdTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> directorStorage.getDirector(1L)
        );
    }
}
