package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.LoginValid;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotBlank(message = "email should not be blank")
    @Email(message = "wrong email pattern")
    private String email;
    @NotBlank(message = "login should be not blank")
    @LoginValid(message = "login should not have spaces")
    private String login;
    private String name;
    @Past(message = "birthday should be in the past")
    private LocalDate birthday;
}
