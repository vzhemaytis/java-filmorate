package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();
    void addNewFilm(Film film);
    void updateFilm(Film film);
    Film findFilm(Long id);
    void addLike(Long id, Long userId);
    void deleteLike(Long id, Long userId);
    List<Film> getPopular(Integer count);

}
