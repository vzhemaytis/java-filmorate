package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();
    Film addNewFilm(Film film);
    Film updateFilm(Film film);
    Film findFilm(Long id);
    void addLike(Long filmId, Long userId);
    void deleteLike(Long filmId, Long userId);
    List<Film> getPopular(Integer count);
    List<Film> getCommonFilms(Long userId, Long friendId);
    void deleteFilm(Long filmId);
}
