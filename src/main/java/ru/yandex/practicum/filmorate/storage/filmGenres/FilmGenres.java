package ru.yandex.practicum.filmorate.storage.filmGenres;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenres {
    Set<Genre> getFilmGenres(Integer filmId);
}
