package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addNewFilm(Film film) {
        return filmStorage.addNewFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film findFilm(Long id) {
        return filmStorage.findFilm(id);
    }

    public void addLike(Long id, Long userId) {
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getPopular(count);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void deleteFilm(Long filmId) {
        filmStorage.deleteFilm(filmId);
    }
}
