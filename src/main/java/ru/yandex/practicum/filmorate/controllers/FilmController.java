package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        log.info("get list of films");
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film addNewFilm(@RequestBody @NotNull @Valid Film film) {
        film.setId(getFilmId());
        films.put(film.getId(), film);
        log.info("add new film - " + film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @NotNull @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Not found film with id " + film.getId());
            throw new NotFoundException("Film not found");
        }
        films.put(film.getId(), film);
        log.info("update film - " + film);
        return film;
    }

    private int getFilmId() {
        return filmId++;
    }
}
