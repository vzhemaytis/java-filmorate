package ru.yandex.practicum.filmorate.storage.filmGenres.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmGenres.FilmGenres;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
public class FilmGenresDb implements FilmGenres {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    public FilmGenresDb(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public Set<Genre> getFilmGenres(Integer filmId) {
        String sql = "select * from FILM_GENRES where FILM_ID = ?";
        List<FilmGenre> filmGenres = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmGenre(rs), filmId);
        return filmGenres.stream()
                .map(FilmGenre::getGenreId)
                .map(genreStorage::findGenre)
                .collect(Collectors.toSet());
    }

    private FilmGenre makeFilmGenre(ResultSet rs) throws SQLException {
        return new FilmGenre(rs.getInt("FILM_ID"), rs.getInt("GENRE_ID"));
    }
}
