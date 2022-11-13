package ru.yandex.practicum.filmorate.storage.friend;

public interface FriendStorage {
    void addFriend(Long id, Long friendId);
    void deleteFriend(Long id, Long friendId);
}
