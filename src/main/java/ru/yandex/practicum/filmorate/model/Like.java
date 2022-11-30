package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    private Long filmId;
    private Long userId;

    public Like(Long filmId, Long userId) {
        this.filmId = filmId;
        this.userId = userId;
    }
}
