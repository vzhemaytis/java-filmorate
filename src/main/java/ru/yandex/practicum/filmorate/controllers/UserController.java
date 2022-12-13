package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable("id") Long id) {
        log.info("get user with id = {}", id);
        return userService.findUser(id);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("get list of users");
        return userService.getUsers();
    }

    @PostMapping
    public User addNewUser(@RequestBody @NotNull @Valid User user) {
        userNameCheck(user);
        log.info("add new user - {}", user);
        return userService.addNewUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @NotNull @Valid User user) {
        userNameCheck(user);
        log.info("update user - {}", user);
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId) {
        log.info("add friendship to user with id = {} and friendId = {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId) {
        log.info("delete friendship from user with id = {} and friendId = {}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Long id) {
        log.info("get friends list of user with id = {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getUserFriends(@PathVariable("id") Long id,
                                     @PathVariable("otherId") Long otherId) {
        log.info("get list of common friends of users with id = {} and id = {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId){
        log.info("delete user from users with id = {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable("id") Long id) {
        log.info("get feed of user with id = {}", id);
        return userService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable("id") Long id) {
        log.info("get list of recommendations of users with id = {}", id);
        return userService.getRecommendationsFilms(id);
    }

    private void userNameCheck(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

}
