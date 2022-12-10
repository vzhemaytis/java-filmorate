package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
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
        user.setId(getUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.get(user.getId()) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, user.getId()));
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUser(Long id) {
        if (users.get(id) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, id));
        }
        return users.get(id);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        if (users.get(id) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, id));
        }
        if (users.get(friendId) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, friendId));
        }
        users.get(id).addFriend(friendId);
        users.get(friendId).addFriend(id);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        if (users.get(id) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, id));
        }
        if (users.get(friendId) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, friendId));
        }
        users.get(id).deleteFriend(friendId);
        users.get(friendId).deleteFriend(id);
    }

    @Override
    public List<User> getUserFriends(Long id) {
        if (users.get(id) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, id));
        }
        return users.values().stream().
                filter(user -> users.get(id).getFriends().contains(user.getId())).
                collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        if (users.get(id) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, id));
        }
        if (users.get(otherId) == null) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, otherId));
        }
        return users.get(id).getFriends().stream().
                filter(userId -> users.get(otherId).getFriends().contains(userId)).
                map(users::get).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
    }

    private long getUserId() {
        return userId++;
    }
}
