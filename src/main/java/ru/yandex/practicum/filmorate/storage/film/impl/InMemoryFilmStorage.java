package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private long filmId = 1;
    private final Map<Long, Film> films = new HashMap<>();

    private Long getFilmId() {
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
    public Film addNewFilm(Film film) {
        film.setId(getFilmId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.get(film.getId()) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Film.class, film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findFilm(Long id) {
        if (films.get(id) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Film.class, id));
        }
        return films.get(id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        User user = userStorage.findUser(userId); // Проверка наличия юзера
        if (films.get(id) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Film.class, id));
        }
        films.get(id).addLike(user.getId());
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        User user = userStorage.findUser(userId); // Проверка наличия юзера
        if (films.get(id) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Film.class, id));
        }
        films.get(id).deleteLike(user.getId());
    }

    @Override
    public List<Film> getPopular(Integer count) {
        return films.values().stream().
                sorted(Comparator.comparingInt(Film::getPopularity).reversed()).
                limit(count).
                collect(Collectors.toList());
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return null;
    }

    @Override
    public void deleteFilm(Long filmId) {
    }
    public List<Film> getFilmsByDirectorSortedByType(Integer directorId, String sortType) {
        return null;
    }

    @Override
    public List<Film> getFilmsByFilters(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        return null;
    }

    @Override
    public List<Film> search(String query, List<String> searchCriteria) {
        return null;
    }

    @Override
    public List<Long> getLikeFilmsUsersId(Long id) {
        return null;
    }

    @Override
    public List<Long> getRecommendations(Long userId) {
        return null;
    }
}
