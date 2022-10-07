package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validators.UserValidator.isUserValid;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        log.info("get list of users");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addNewUser(@RequestBody @NotNull User user) {
        if (isUserValid(user)) {
            user.setId(getUserId());
            users.put(user.getId(), user);
            log.info("add new user - " + user);
        } else {
            throw new ValidationException("A validation error occurred");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @NotNull User user) {
        if (isUserValid(user) && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("update user - " + user);
        } else {
            throw new ValidationException("A validation error occurred");
        }
        return user;
    }

    private int getUserId() {
        return userId++;
    }
}
