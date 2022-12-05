package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director addToFilms(@Valid @RequestBody Director director) {
        log.debug("Добавлен режиссер: " + director);
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director putToFilm(@Valid @RequestBody Director director) {
        log.debug("Данные режиссера: " + director.getName() + "обновлен");
        return directorService.updateDirector(director);
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        log.debug("Запрошены режиссеры");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Long id) {
        log.debug("Запрошены режиссер " + id);
        return directorService.getDirector(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
        log.debug("Удален режиссер " + id);
    }
}
