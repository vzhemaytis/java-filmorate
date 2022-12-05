package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorageTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest extends FilmStorageTest<FilmDbStorage> {
    private final LocalDate releaseDate = LocalDate.of(2021, 12, 12);

    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    Film film;

    @BeforeEach
    void setup() {
        film = Film.builder()
                .name("name")
                .description("desc")
                .duration(100)
                .releaseDate(releaseDate)
                .mpa(new Mpa(1))
                .genres(Set.of(new Genre(1)))
                .build();
    }

    @AfterEach
    void deleteData() {
        jdbcTemplate.update("drop all objects");
        jdbcTemplate.update("runscript from 'src/test/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/data.sql'");
    }

    @Test
    @DisplayName("01) Проверка получения всех фильмов из БД")
    void getFilmsTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<Film> films = filmStorage.getFilms();
        assertEquals(15, films.size());
    }

    @Test
    @DisplayName("02) Проверка получения пустого списка фильмов из БД")
    void getFilmsTest2() {
        List<Film> films = filmStorage.getFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    @DisplayName("03) Проверка добавления нового фильма в БД")
    void addFilmTest() {
        Film addedFilm = filmStorage.addNewFilm(film);
        List<Film> films = filmStorage.getFilms();
        assertAll(
                () -> assertEquals(1, films.size()),
                () -> assertEquals(1L, addedFilm.getId()),
                () -> assertEquals("name", addedFilm.getName()),
                () -> assertEquals("desc", addedFilm.getDescription()),
                () -> assertEquals(100, addedFilm.getDuration()),
                () -> assertEquals(releaseDate, addedFilm.getReleaseDate()),
                () -> assertEquals(1, addedFilm.getMpa().getId()),
                () -> assertEquals(1, addedFilm.getGenres().size())
        );
    }

    @Test
    @DisplayName("04) Проверка обновления фильма в БД")
    void updateFilmTest() {
        filmStorage.addNewFilm(film);
        LocalDate newDate = LocalDate.of(2000, 10, 12);
        Film updatedFilm = Film.builder()
                .id(1L)
                .name("new name")
                .description("new desc")
                .duration(101)
                .releaseDate(newDate)
                .mpa(new Mpa(2))
                .genres(Set.of(new Genre(2), new Genre(3)))
                .build();
        Film savedUpdatedFilm = filmStorage.updateFilm(updatedFilm);
        assertAll(
                () -> assertEquals(1L, savedUpdatedFilm.getId()),
                () -> assertEquals("new name", savedUpdatedFilm.getName()),
                () -> assertEquals("new desc", savedUpdatedFilm.getDescription()),
                () -> assertEquals(101, savedUpdatedFilm.getDuration()),
                () -> assertEquals(newDate, savedUpdatedFilm.getReleaseDate()),
                () -> assertEquals(2, savedUpdatedFilm.getMpa().getId()),
                () -> assertEquals(2, savedUpdatedFilm.getGenres().size())
        );
    }

    @Test
    @DisplayName("05) Проверка получения ошибки при обновлении несуществующего фильма")
    void updateFilmTest2() {
        film.setId(1L);
        assertThrows(
                EntityNotFoundException.class,
                () -> filmStorage.updateFilm(film)
        );
    }

    @Test
    @DisplayName("06) Проверка получения фильма по ID")
    void findFilmTest() {
        filmStorage.addNewFilm(film);
        Film findFilm = filmStorage.findFilm(1L);
        assertAll(
                () -> assertEquals(1L, findFilm.getId()),
                () -> assertEquals("name", findFilm.getName()),
                () -> assertEquals("desc", findFilm.getDescription()),
                () -> assertEquals(100, findFilm.getDuration()),
                () -> assertEquals(releaseDate, findFilm.getReleaseDate()),
                () -> assertEquals(1, findFilm.getMpa().getId()),
                () -> assertEquals(1, findFilm.getGenres().size())
        );
    }

    @Test
    @DisplayName("07) Проверка получения фильма по несуществующему ID")
    void findFilmTest2() {
        assertThrows(
                EntityNotFoundException.class,
                () -> filmStorage.findFilm(1L)
        );
    }

    @Test
    @DisplayName("08) Проверка добавления лайка в фильму")
    void addLikeTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        filmStorage.addLike(2L, 1L);
        filmStorage.addLike(2L, 2L);
        Film likedFilm = filmStorage.findFilm(2L);
        assertEquals(Set.of(1L, 2L), likedFilm.getLikes());
    }

    @Test
    @DisplayName("09) Проверка получения ошибки при добавления лайка несуществующему фильму")
    void addLikeTest2() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> filmStorage.addLike(16L, 1L)
        );
    }

    @Test
    @DisplayName("10) Проверка получения ошибки при добавления лайка от несуществующему пользователя")
    void addLikeTest3() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> filmStorage.addLike(15L, 11L)
        );
    }

    @Test
    @DisplayName("11) Проверка удаления лайка в фильму")
    void deleteLikeTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        filmStorage.addLike(2L, 1L);
        filmStorage.addLike(2L, 2L);
        Film likedFilm = filmStorage.findFilm(2L);
        assertEquals(Set.of(1L, 2L), likedFilm.getLikes());
        filmStorage.deleteLike(2L, 2L);
        likedFilm = filmStorage.findFilm(2L);
        assertEquals(Set.of(1L), likedFilm.getLikes());
    }

    @Test
    @DisplayName("12) Проверка получения ошибки при удалении лайка несуществующему фильму")
    void deleteLikeTest2() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> filmStorage.deleteLike(16L, 1L)
        );
    }

    @Test
    @DisplayName("13) Проверка получения ошибки при удалении лайка от несуществующему пользователя")
    void deleteLikeTest3() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> filmStorage.deleteLike(15L, 11L)
        );
    }

    @Test
    @DisplayName("14) Проверка получения ошибки при удалении несуществующего лайка")
    void deleteLikeTest4() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> filmStorage.deleteLike(15L, 10L)
        );
    }


    @Test
    @DisplayName("15) Проверка получения списка популярных фильмов")
    void getPopularTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<Film> popular = filmStorage.getPopular(10);
        assertEquals(4L, popular.get(0).getId());
        assertEquals(1L, popular.get(1).getId());
        assertEquals(10L, popular.get(2).getId());
        assertEquals(13L, popular.get(3).getId());
    }

    @Test
    @DisplayName("16) Проверка получения пустого списка популярных фильмов")
    void getPopularTest2() {
        List<Film> popular = filmStorage.getPopular(10);
        assertEquals(0, popular.size());
    }

    @Test
    @DisplayName("17) Проверка изменения очередности фильмов в списке популярных фильмов")
    void getPopularTest3() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<Film> popular = filmStorage.getPopular(10);
        assertEquals(10, popular.size());
        assertEquals(4L, popular.get(0).getId());
        filmStorage.addLike(1L, 4L);
        filmStorage.addLike(1L, 5L);
        filmStorage.addLike(1L, 6L);
        popular = filmStorage.getPopular(10);
        assertEquals(10, popular.size());
        assertEquals(1L, popular.get(0).getId());
    }
    @Test
    @DisplayName("18) Проверка удаления фильма")
    void deleteFilmTest(){
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        Integer listFilmsBeforeDelete = filmStorage.getFilms().size();
        filmStorage.deleteFilm(1L);
        Integer listFilmsAfterDelete = filmStorage.getFilms().size();
        assertNotEquals(listFilmsBeforeDelete, listFilmsAfterDelete);
        assertEquals(filmStorage.getFilms().size(), 14);
    }
}
