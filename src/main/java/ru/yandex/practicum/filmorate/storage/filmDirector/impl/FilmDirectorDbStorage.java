package ru.yandex.practicum.filmorate.storage.filmDirector.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.filmDirector.FilmDirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Primary
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilmDirectors(Long filmId, Set<Director> directors) {
        deleteFilmDirectors(filmId);
        try {
            if (directors != null) {
                String sqlQuery = "merge into FILM_DIRECTORS(FILM_ID, DIRECTOR_ID) " +
                        "values (?, ?)";
                directors.forEach(director -> jdbcTemplate.update(sqlQuery,
                        filmId, director.getId()));
            }
        } catch (DataAccessException ex) {
            throw new EntityNotFoundException(String.format("At least one of director from %s not found", directors));
        }
    }

    @Override
    public Set<Director> getFilmDirector(Long filmId) {
        String sql = "select D.* from FILM_DIRECTORS FD" +
                " join DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID" +
                " where FILM_ID = ?";
        List<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), filmId);
        return new HashSet<>(directors);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return new Director(
                rs.getLong("director_id"),
                Optional.of(rs.getString("director_name")).orElse(null)
        );
    }

    private void deleteFilmDirectors(Long filmId) {
        String sqlQuery = "delete from FILM_DIRECTORS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
