package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    @NotBlank(message = "name should be not blank")
    private String name;
    @Size(max = 200, message = "description should be shorter 200 letters")
    private String description;
    @ReleaseDateValid(message = "release date should be past 28.12.1895")
    private LocalDate releaseDate;
    @Positive(message = "duration should be positive")
    private int duration;
    private int rate;
    @NotNull(message = "Mpa should be not null")
    private Mpa mpa;
    private Set<Genre> genres;
    private final Set<Long> likes = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void deleteLike(Long userId) {
        try {
            likes.remove(userId);
        } catch (NullPointerException e) {
            throw new EntityNotFoundException(
                    String.format("%s with id= %s not found to delete like", User.class, userId));
        }
    }

    public int getPopularity() {
        return likes.size();
    }
}
