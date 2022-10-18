package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.FriendNotFoundException;
import ru.yandex.practicum.filmorate.validator.LoginValid;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    @NotBlank(message = "email should not be blank")
    @Email(message = "wrong email pattern")
    private String email;
    @NotBlank(message = "login should be not blank")
    @LoginValid(message = "login should not have spaces")
    private String login;
    private String name;
    @Past(message = "birthday should be in the past")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Long friendId) {
        if (!friends.contains(friendId)) {
            throw new FriendNotFoundException(this.id, friendId);
        }
        friends.remove(friendId);
    }
}
