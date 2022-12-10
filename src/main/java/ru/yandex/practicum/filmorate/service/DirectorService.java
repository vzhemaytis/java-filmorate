package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director addDirector(Director director) {
        Director result = directorStorage.addDirector(director);
        log.debug("Added director " + result.getName());
        return result;
    }

    public Director updateDirector(Director director) {
        Director result = directorStorage.updateDirector(director);
        log.debug("Director " + result.getName() + " updated");
        return result;
    }

    public Director getDirector(Long id) {
        Director director = directorStorage.getDirector(id).orElse(null);
        log.debug("Requested director with id: " + id);
        return director;
    }

    public List<Director> getDirectors() {
        log.debug("Requested all directors");
        return directorStorage.getDirectors();
    }

    public void deleteDirector(Long id) {
        directorStorage.deleteDirector(id);
        log.debug("Director with id: " + id + " deleted");
    }
}
