package ru.yandex.practicum.filmorate.exceptions;

public class LikeNotFoundException extends RuntimeException{
    private final Long userId;
    private final Long id;

    public LikeNotFoundException(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getId() {
        return id;
    }
}
