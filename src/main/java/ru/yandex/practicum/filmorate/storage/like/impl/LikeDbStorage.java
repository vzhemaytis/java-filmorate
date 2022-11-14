package ru.yandex.practicum.filmorate.storage.like.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Component
@Primary
@Slf4j
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        User user = userStorage.findUser(userId);
        String sqlQuery = "insert into LIKES (FILM_ID, USER_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, user.getId());
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        User user = userStorage.findUser(userId);
        String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, user.getId());
    }

    @Override
    public Set<Long> getLikes(Long filmId) {
        String sql = "select USER_ID from LIKES where FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("USER_ID"), filmId));
    }


}
