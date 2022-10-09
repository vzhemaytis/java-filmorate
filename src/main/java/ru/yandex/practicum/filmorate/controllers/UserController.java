package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        log.info("get list of users");
        return List.copyOf(users.values());
    }

    @PostMapping
    public User addNewUser(@RequestBody @NotNull @Valid User user) {
        userNameCheck(user);
        user.setId(getUserId());
        users.put(user.getId(), user);
        log.info("add new user - " + user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @NotNull @Valid User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Not found user with id: " + user.getId());
            throw new NotFoundException("User not found");
        }
        userNameCheck(user);
        users.put(user.getId(), user);
        log.info("update user - " + user);
        return user;
    }

    private void userNameCheck(User user) {
        if (user.getName() == null || user.getName().isEmpty()) { // Name check
            user.setName(user.getLogin());
        }
    }
    private int getUserId() {
        return userId++;
    }
}
