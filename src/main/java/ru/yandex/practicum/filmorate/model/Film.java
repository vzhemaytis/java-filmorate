package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank(message = "name should be not blank")
    private String name;
    @Size(max = 200, message = "description should be shorter 200 letters")
    private String description;
    @ReleaseDateValid(message = "release date should be past 28.12.1895")
    private LocalDate releaseDate;
    @Positive(message = "duration should be positive")
    private int duration;
}
