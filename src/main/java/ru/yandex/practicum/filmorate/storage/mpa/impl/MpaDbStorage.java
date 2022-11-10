package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("mpaDbStorage")
@Slf4j
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
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from MPA_RATING where MPA_ID = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("MPA_ID"),
                    mpaRows.getString("MPA_NAME"));
            log.info("Найден рейтинг MPA: {} {}", mpa.getId(), mpa.getName());
            return mpa;
        } else {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", Mpa.class, id));
        }
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Integer mpaId = rs.getInt("MPA_ID");
        String mpaName = rs.getString("MPA_NAME");
        return new Mpa(mpaId, mpaName);
    }
}
