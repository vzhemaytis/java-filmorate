package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validators.FilmValidator.isFilmValid;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        log.info("get list of films");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addNewFilm(@RequestBody @NotNull Film film) {
        if (isFilmValid(film)) {
            film.setId(getFilmId());
            films.put(film.getId(), film);
            log.info("add new film - " + film);
        } else {
            throw new ValidationException("A validation error occurred");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @NotNull Film film) {
        if (isFilmValid(film) && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("update film - " + film);
        } else {
            throw new ValidationException("A validation error occurred");
        }
        return film;
    }

    private int getFilmId() {
        return filmId++;
    }
}
