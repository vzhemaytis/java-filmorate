package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("get list of films");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable("id") Long id) {
        log.info("get film with id = {}", id);
        return filmService.findFilm(id);
    }

    @PostMapping
    public Film addNewFilm(@RequestBody @NotNull @Valid Film film) {
        log.info("add new film - {}", film);
        return filmService.addNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @NotNull @Valid Film film) {
        log.info("update film - {}", film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id,
                        @PathVariable("userId") Long userId) {
        log.info("add like to film with id = {} from user with id = {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long id,
                           @PathVariable("userId") Long userId) {
        log.info("delete like from film with id = {} from user with id = {}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId){
        log.info("get common films user = {} with friend = {} ", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable("filmId") Long filmId) {
        log.info("delete film from films with id = {}", filmId);
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirectorSortedByYear(@PathVariable Integer directorId,
                                                     @RequestParam(name = "sortBy") String sortType) {
        log.info("Get films by director sort by year");
        return filmService.getFilmsByDirectorSortedByYear(directorId, sortType);

    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam(name = "query") String query,
                             @RequestParam(name = "by") List<String> searchCriteria) {
        log.info("Search {}, which contains {}", searchCriteria, query);
        return filmService.search(query, searchCriteria);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsByFilter(@RequestParam(defaultValue = "10", required = false) Integer count,
                                       @RequestParam Optional<Integer> genreId,
                                       @RequestParam Optional<Integer> year) {
        log.info("get {} most popular films", count);
        return filmService.getFilmsByFilters(count, genreId, year);
    }
}
