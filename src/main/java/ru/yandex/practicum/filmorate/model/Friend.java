package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {
    private Integer userId;
    private Integer friendId;

    public Friend(Integer userId, Integer friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
