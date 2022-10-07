package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895,12,28);

    public static boolean isFilmValid(Film film) {
        if (film == null) { // NotNull check
            return false;
        }
        if (film.getName() == null || film.getName().isEmpty()) { // Name check
            return false;
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) { // Description check
            return false;
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) { // Release date check
            return false;
        }
        if (film.getDuration() <= 0) { // Positive duration check
            return false;
        }
        return true;
    }
}
