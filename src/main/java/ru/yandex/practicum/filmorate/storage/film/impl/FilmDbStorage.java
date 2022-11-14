package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;


    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, FilmGenreStorage filmGenreStorage, LikeStorage likeStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select * from FILMS order by FILM_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addNewFilm(Film film) {
        String sqlQuery = "insert into FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID) " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        filmGenreStorage.addFilmGenre(filmId, film.getGenres());
        return findFilm(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILMS set " +
                "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ?, MPA_ID = ? " +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRate()
                , film.getMpa().getId()
                , film.getId());
        filmGenreStorage.addFilmGenre(film.getId(), film.getGenres());
        return findFilm(film.getId());
    }

    @Override
    public Film findFilm(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from FILMS where FILM_ID = ?", id);
        if (!filmRows.next()) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Film.class, id));
        }
        Film film = Film.builder()
                .id(filmRows.getLong("FILM_ID"))
                .name(filmRows.getString("FILM_NAME"))
                .description(filmRows.getString("DESCRIPTION"))
                .releaseDate(Objects.requireNonNull(filmRows.getDate("RELEASE_DATE")).toLocalDate())
                .duration(filmRows.getInt("DURATION"))
                .rate(filmRows.getInt("RATE"))
                .mpa(mpaStorage.findMpa(filmRows.getInt("MPA_ID")))
                .genres(filmGenreStorage.getFilmGenres(filmRows.getLong("FILM_ID")))
                .likes(likeStorage.getLikes(filmRows.getLong("FILM_ID")))
                .build();
        log.info("Найден фильм: {} {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = findFilm(filmId);
        likeStorage.addLike(film.getId(), userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = findFilm(filmId);
        likeStorage.deleteLike(film.getId(), userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        String sql = "select *\n" +
                "from FILMS as F\n" +
                "left join (select FILM_ID, count(USER_ID) as POPULARITY\n" +
                "           from LIKES\n" +
                "           group by FILM_ID\n" +
                "           ) as P on F.FILM_ID = P.FILM_ID\n" +
                "order by P.POPULARITY desc\n" +
                "limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .rate(rs.getInt("RATE"))
                .mpa(mpaStorage.findMpa(rs.getInt("MPA_ID")))
                .genres(filmGenreStorage.getFilmGenres(rs.getLong("FILM_ID")))
                .likes(likeStorage.getLikes(rs.getLong("FILM_ID")))
                .build();
    }
}
