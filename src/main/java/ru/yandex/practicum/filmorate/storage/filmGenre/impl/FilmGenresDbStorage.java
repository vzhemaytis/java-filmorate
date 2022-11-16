package ru.yandex.practicum.filmorate.storage.filmGenre.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
public class FilmGenresDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    public FilmGenresDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public void addFilmGenre(Long filmId, Set<Genre> genres) {
        deleteFilmGenres(filmId);
        if (genres != null) {
            String sqlQuery = "merge into FILM_GENRES(FILM_ID, GENRE_ID) " +
                    "values (?, ?)";
            genres.forEach(genre -> jdbcTemplate.update(sqlQuery,
                    filmId, genre.getId()));
        }
    }

    @Override
    public Set<Genre> getFilmGenres(Long filmId) {
        String sql = "select * from FILM_GENRES where FILM_ID = ?";
        List<FilmGenre> filmGenres = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmGenre(rs), filmId);
        return filmGenres.stream()
                .map(FilmGenre::getGenreId)
                .map(genreStorage::findGenre)
                .collect(Collectors.toSet());
    }

    private FilmGenre makeFilmGenre(ResultSet rs) throws SQLException {
        return new FilmGenre(rs.getLong("FILM_ID"), rs.getInt("GENRE_ID"));
    }

    private void deleteFilmGenres(Long filmId) {
        String sqlQuery = "delete from FILM_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

}
