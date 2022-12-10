package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;


    public FilmDbStorage(JdbcTemplate jdbcTemplate
            , MpaStorage mpaStorage
            , FilmGenreStorage filmGenreStorage
            , LikeStorage likeStorage) {
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
        try {
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
        } catch (DataAccessException ex) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Film.class, film.getId()));
        }
        return findFilm(film.getId());
    }

    @Override
    public Film findFilm(Long id) {
        String sql = "select * from FILMS where FILM_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        } catch (DataAccessException e) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Film.class, id));
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = findFilm(filmId);
        likeStorage.addLike(film.getId(), userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = findFilm(filmId);
        if(!film.getLikes().contains(userId)) {
            throw new EntityNotFoundException(String.format("Like on film id = %s from user id = %s", filmId, userId));
        }
        likeStorage.deleteLike(film.getId(), userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        String sql = "select * " +
                "from FILMS as F " +
                "left join (select FILM_ID, count(USER_ID) as POPULARITY " +
                "           from LIKES " +
                "           group by FILM_ID) as P " +
                "           on F.FILM_ID = P.FILM_ID " +
                "order by P.POPULARITY desc " +
                "limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    public List<Long> getRecommendations(Long userId) {
        String recommendationFilm = "SELECT fr.film_id FROM LIKES as fr " +
                "WHERE fr.user_id = (SELECT fl.user_id FROM LIKES as fl WHERE fl.user_id <> ? " +
                "AND fl.film_id IN (SELECT f.film_id FROM LIKES as f WHERE f.user_id = ?) " +
                "GROUP BY fl.user_id " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT 1)";
        return jdbcTemplate.queryForList(recommendationFilm, Long.class, userId, userId);
    }

    public List<Long> getLikeFilmsUsersId(Long id) {
        String likeFilms = "SELECT film_id FROM LIKES WHERE user_id = ?";
        return jdbcTemplate.queryForList(likeFilms, Long.class, id);
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
