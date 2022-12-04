package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();
    User addNewUser(User user);
    User updateUser(User user);
    User findUser(Long id);
    void addFriend(Long id, Long friendId);
    void deleteFriend(Long id, Long friendId);
    List<User> getUserFriends(Long id);
    List<User> getCommonFriends(Long id, Long otherId);
    List<Event> getFeed(Long id);

}
