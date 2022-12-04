package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addNewUser(User user) {
        return userStorage.addNewUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User findUser(Long id) {
        return userStorage.findUser(id);
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getUserFriends(Long id) {
        return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    public List<Event> getFeed(Long id) { return userStorage.getFeed(id); }
}
