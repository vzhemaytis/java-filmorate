package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private long filmId = 1;
    private final Map<Long, Film> films = new HashMap<>();

    private long getFilmId() {
        return filmId++;
    }
    private final UserStorage userStorage;

    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public void addNewFilm(Film film) {
        film.setId(getFilmId());
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(film.getId());
        }
        films.put(film.getId(), film);
    }

    @Override
    public Film findFilm(Long id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException(id);
        }
        return films.get(id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        User user = userStorage.findUser(userId); // Проверка наличия юзера
        films.get(id).addLike(user.getId());
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        User user = userStorage.findUser(userId); // Проверка наличия юзера
        films.get(id).deleteLike(user.getId());
    }

    @Override
    public List<Film> getPopular(Integer count) {
        return films.values().stream().
                sorted(Comparator.comparingInt(Film::getPopularity).reversed()).
                limit(count).
                collect(Collectors.toList());
    }
}
