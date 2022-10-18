package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User addNewUser(User user) {
        userNameCheck(user);
        user.setId(getUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }
        userNameCheck(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUser(Long id) {
        if (!users.containsKey(id)){
            throw new UserNotFoundException(id);
        }
        return users.get(id);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        if (!users.containsKey(friendId)) {
            throw new UserNotFoundException(friendId);
        }
        users.get(id).addFriend(friendId);
        users.get(friendId).addFriend(id);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        if (!users.containsKey(friendId)) {
            throw new UserNotFoundException(friendId);
        }
        users.get(id).deleteFriend(friendId);
        users.get(friendId).deleteFriend(id);
    }

    @Override
    public List<User> getUserFriends(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        return users.values().stream().
                filter(user -> users.get(id).getFriends().contains(user.getId())).
                collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        if (!users.containsKey(otherId)) {
            throw new UserNotFoundException(otherId);
        }
        return users.get(id).getFriends().stream().
                filter(userId -> users.get(otherId).getFriends().contains(userId)).
                map(users::get).collect(Collectors.toList());
    }

    private long getUserId() {
        return userId++;
    }

    private void userNameCheck(User user) {
        if (user.getName() == null || user.getName().isEmpty()) { // Name check
            user.setName(user.getLogin());
        }
    }


}
