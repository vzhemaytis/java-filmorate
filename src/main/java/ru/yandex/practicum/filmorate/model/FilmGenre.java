package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private Long filmId;
    private Integer genreId;

    public FilmGenre(Long filmId, Integer genreId) {
        this.filmId = filmId;
        this.genreId = genreId;
    }
}
