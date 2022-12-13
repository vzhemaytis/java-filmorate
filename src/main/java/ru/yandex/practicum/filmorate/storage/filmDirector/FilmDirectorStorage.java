package ru.yandex.practicum.filmorate.storage.filmDirector;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

public interface FilmDirectorStorage {
    void addFilmDirectors(Long filmId, Set<Director> directors);

    Set<Director> getFilmDirector(Long filmId);
}
