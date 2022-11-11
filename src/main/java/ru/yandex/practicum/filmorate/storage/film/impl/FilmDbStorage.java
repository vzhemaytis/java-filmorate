package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmGenres.FilmGenres;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final FilmGenres filmGenres;


    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, FilmGenres filmGenres) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.filmGenres = filmGenres;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select * from FILMS order by FILM_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public void addNewFilm(Film film) {

    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public Film findFilm(Long id) {
        return null;
    }

    @Override
    public void addLike(Long id, Long userId) {

    }

    @Override
    public void deleteLike(Long id, Long userId) {

    }

    @Override
    public List<Film> getPopular(Integer count) {
        return null;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE"))
                .duration(rs.getInt("DURATION"))
                .mpa(mpaStorage.findMpa(rs.getInt("MPA_ID")))
                .genres(filmGenres.getFilmGenres(rs.getInt("FILM_ID")))
                .build();
    }
}
