package ru.yandex.practicum.filmorate.impl.utils;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidatorTestUtils {

    public static Film getFilmDto(int id, String name, String description, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }
}
