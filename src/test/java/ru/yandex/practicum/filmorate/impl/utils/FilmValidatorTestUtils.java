package ru.yandex.practicum.filmorate.impl.utils;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidatorTestUtils {

    public static Film getFilmDto(Long id, String name, String description, LocalDate releaseDate, int duration) {
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();
    }
}
