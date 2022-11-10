package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {

    private Integer userId;
    private Integer friendId;
    private Boolean isAccepted;

}
