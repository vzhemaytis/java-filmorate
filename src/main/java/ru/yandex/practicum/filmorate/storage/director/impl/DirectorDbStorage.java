package ru.yandex.practicum.filmorate.storage.director.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director addDirector(Director director) {
        String sql = "insert into DIRECTORS (DIRECTOR_NAME) values (?);";
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"DIRECTOR_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, kh);
        director.setId(Objects.requireNonNull(kh.getKey()).longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if(getDirector(director.getId()).isPresent()) {
            String sql = "update DIRECTORS set DIRECTOR_NAME = ? where DIRECTOR_ID = ?";
            jdbcTemplate.update(sql, director.getName(), director.getId());
        } else {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Director.class, director.getId()));
        }
        return director;
    }

    @Override
    public Optional<Director> getDirector(Long id) {
        try {
            Director director = jdbcTemplate.queryForObject(
                    "select * from DIRECTORS" +
                            " where DIRECTOR_ID = ?",
                    (rs, rowNum) -> makeDirector(rs),
                    id
            );
            if(director != null) {
                return Optional.of(director);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Director.class, id));
        }
        return Optional.empty();
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "select * from DIRECTORS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public void deleteDirector(Long id) {
        try {
            String sqlFD = "delete from FILM_DIRECTORS where DIRECTOR_ID = ?";
            jdbcTemplate.update(sqlFD, id);
            String sql = "delete from DIRECTORS where DIRECTOR_ID = ?";
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException ex) {
            System.out.println(ex.getMessage());
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Director.class, id));
        }

    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return new Director(
                rs.getLong("director_id"),
                rs.getString("director_name")
        );
    }
}
