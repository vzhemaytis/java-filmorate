package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private Integer filmId;
    private Integer genreId;

    public FilmGenre(Integer filmId, Integer genreId) {
        this.filmId = filmId;
        this.genreId = genreId;
    }
}
