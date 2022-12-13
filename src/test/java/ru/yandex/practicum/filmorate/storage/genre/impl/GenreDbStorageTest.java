package ru.yandex.practicum.filmorate.storage.genre.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorageTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest extends GenreStorageTest<GenreDbStorage> {

    private final GenreStorage genreStorage;

    @Test
    @DisplayName("1) Получение списка жанров")
    void getAllGenresTest() {
        List<Genre> genres = genreStorage.getGenres();
        assertAll(
                () -> assertEquals(6, genres.size()),
                () -> assertEquals(1, genres.get(0).getId()),
                () -> assertEquals("Комедия", genres.get(0).getName()),
                () -> assertEquals(2, genres.get(1).getId()),
                () -> assertEquals("Драма", genres.get(1).getName()),
                () -> assertEquals(3, genres.get(2).getId()),
                () -> assertEquals("Мультфильм", genres.get(2).getName()),
                () -> assertEquals(4, genres.get(3).getId()),
                () -> assertEquals("Триллер", genres.get(3).getName()),
                () -> assertEquals(5, genres.get(4).getId()),
                () -> assertEquals("Документальный", genres.get(4).getName()),
                () -> assertEquals(6, genres.get(5).getId()),
                () -> assertEquals("Боевик", genres.get(5).getName())
        );
    }

    @Test
    @DisplayName("2) Получение жанра по ID")
    void findGenreTest() {
        Genre genre = genreStorage.findGenre(1);
        assertAll(
                () -> assertEquals(1, genre.getId()),
                () -> assertEquals("Комедия", genre.getName())
        );
    }

    @Test
    @DisplayName("3) Получение ошибки при запросе несуществующего жанра")
    void findGenreThrowsEntityNotFoundTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> genreStorage.findGenre(7)
        );
    }
}
