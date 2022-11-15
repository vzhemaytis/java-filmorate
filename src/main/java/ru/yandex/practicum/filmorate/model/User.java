package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.validator.LoginValid;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
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
    private Set<Long> friends;

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Long friendId) {
        try {
            friends.remove(friendId);
        } catch (NullPointerException e) {
            throw new EntityNotFoundException(
                    String.format("%s with id= %s not found in friends list of user with id= %s",
                            User.class, friendId, this.id));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
