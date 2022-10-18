package ru.yandex.practicum.filmorate.exceptions;


public class FilmNotFoundException extends RuntimeException{
    private final Long id;

    public FilmNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
