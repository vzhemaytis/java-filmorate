package ru.yandex.practicum.filmorate.storage.mpa.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpas() {
        String sql = "select * from MPA_RATING order by MPA_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa findMpa(Integer id) {
        try {
            String sql = "select * from MPA_RATING where MPA_ID = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), id);
        } catch (DataAccessException ex) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Mpa.class, id));
        }
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("MPA_ID")
                , rs.getString("MPA_NAME"));
    }
}
