package ru.yandex.practicum.filmorate.exceptions;

public class FriendNotFoundException extends RuntimeException{
    private final Long id;
    private final Long friendId;

    public FriendNotFoundException(Long id, Long friendId) {
        this.id = id;
        this.friendId = friendId;
    }

    public Long getId() {
        return id;
    }

    public Long getFriendId() {
        return friendId;
    }
}
