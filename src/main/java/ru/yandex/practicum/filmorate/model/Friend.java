package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {
    private final Integer userId;
    private final Integer friendId;
}
