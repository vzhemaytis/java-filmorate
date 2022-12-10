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
import ru.yandex.practicum.filmorate.storage.filmDirector.FilmDirectorStorage;
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
    private final FilmDirectorStorage filmDirectorsStorage;
    private final LikeStorage likeStorage;


    public FilmDbStorage(JdbcTemplate jdbcTemplate
            , MpaStorage mpaStorage
            , FilmGenreStorage filmGenreStorage
            , FilmDirectorStorage filmDirectorStorage
            , LikeStorage likeStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.filmDirectorsStorage = filmDirectorStorage;
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
        filmDirectorsStorage.addFilmDirectors(filmId, film.getDirectors());
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
            filmDirectorsStorage.addFilmDirectors(film.getId(), film.getDirectors());
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
        if (!film.getLikes().contains(userId)) {
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

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId){
        String sql =
                "with COMMON (COMMONID) as " +
                        "( " +
                        "   select distinct FILM_ID from LIKES where USER_ID = ? " +
                        "   intersect " +
                        "   select distinct FILM_ID from LIKES where USER_ID = ? " +
                        ") " +
                        "select FILMS.FILM_ID, FILMS.FILM_NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, FILMS.DURATION, FILMS.RATE, " +
                        "FILMS.MPA_ID " +
                        "from FILMS " +
                        "inner join MPA_RATING on MPA_RATING.MPA_ID = FILMS.RATE " +
                        "where FILM_ID in (select COMMONID from COMMON) " +
                        "group by FILMS.FILM_ID " +
                        "order by FILMS.RATE desc;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), userId, friendId);
    }

    @Override
    public void deleteFilm(Long filmId) {
        String sql1 = "delete from FILM_GENRES where FILM_ID = ?";
        String sql2 = "delete from LIKES where FILM_ID = ?";
        String sql3 = "delete from FILMS where FILM_ID = ?";
        try {
            jdbcTemplate.update(sql1, filmId);
            jdbcTemplate.update(sql2, filmId);
            jdbcTemplate.update(sql3, filmId);
        } catch (DataAccessException e) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Film.class, filmId));
        }
    }

    public List<Film> getFilmsByDirectorSortedByType(Integer directorId, String sortType) {
        String orderBy = null;
        if (sortType.equals("year")) {
            orderBy = "F.RELEASE_DATE";
        } else if (sortType.equals("likes")) {
            orderBy = "P.POPULARITY desc";
        }
        String sql = "select * from FILM_DIRECTORS FD " +
                "left join FILMS F on F.FILM_ID = FD.FILM_ID " +
                "left join (select FILM_ID, count(USER_ID) as POPULARITY " +
                "           from LIKES " +
                "           group by FILM_ID) as P " +
                "           on F.FILM_ID = P.FILM_ID " +
                "where FD.DIRECTOR_ID =?" +
                "order by " + orderBy;
        List<Film> result = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), directorId);
        if (result.size() > 0) {
            return result;
        } else {
            throw new EntityNotFoundException(
                    String.format("%s with director_id= %s not found", Film.class, directorId)
            );
        }
    }

    @Override
    public List<Film> search(String query, List<String> searchCriteria) {
        try {
            StringBuilder whereSql = new StringBuilder("");
            if (searchCriteria.size() > 0) {
                whereSql.append("where ");
            }
            if (searchCriteria.contains("title")) {
                whereSql.append("F.FILM_NAME like '%").append(query.toLowerCase()).append("%'");
            }
            if (searchCriteria.contains("director")) {
                if (whereSql.indexOf("%") != -1) {
                    whereSql.append(" or ");
                }
                whereSql.append("D.DIRECTOR_NAME like '%").append(query.toLowerCase()).append("%'");
            }
            String sqlQuery = "select * from FILMS F " +
                    "left join FILM_DIRECTORS FD on FD.FILM_ID = F.FILM_ID " +
                    "left join DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    whereSql +
                    " order by FILM_ID desc"
                    ;

            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new EntityNotFoundException("");
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long filmId = rs.getLong("FILM_ID");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .rate(rs.getInt("RATE"))
                .mpa(mpaStorage.findMpa(rs.getInt("MPA_ID")))
                .genres(filmGenreStorage.getFilmGenres(filmId))
                .likes(likeStorage.getLikes(filmId))
                .directors(filmDirectorsStorage.getFilmDirector(filmId))
                .build();
    }
}

