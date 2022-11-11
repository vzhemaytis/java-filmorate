package ru.yandex.practicum.filmorate.storage.filmGenre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreStorage {
    void addFilmGenre(Long filmId, Set<Genre> genres);
    Set<Genre> getFilmGenres(Long filmId);
}
