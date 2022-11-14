package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmStorageTest {

    private final LocalDate releaseDate = LocalDate.of(2021, 12, 12);

    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    Film film;

    @BeforeEach
    void setup(){
        jdbcTemplate.update("runscript from 'src/test/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/data.sql'");
        film = Film.builder()
                .name("name")
                .description("desc")
                .duration(100)
                .releaseDate(releaseDate)
                .mpa(new Mpa(1))
                .build();
    }

    @AfterEach
    void deleteData() {
        jdbcTemplate.update("drop all objects");
    }



    @Test
    void addFilmTest() {
        Film addedFilm = filmStorage.addNewFilm(film);
        assertEquals(1L, addedFilm.getId());
    }

    @Test
    void updateFilmTest() {
        Film addedFilm = filmStorage.addNewFilm(film);
        addedFilm.setName("new name");
        assertEquals(1L, addedFilm.getId());
        Film updatedFilm = filmStorage.updateFilm(addedFilm);
        assertEquals(1L, updatedFilm.getId());

    }

    @Test
    void findFilmTest() {
        filmStorage.addNewFilm(film);
        Film findFilm = filmStorage.findFilm(1L);
        assertEquals("name", findFilm.getName());
    }

}