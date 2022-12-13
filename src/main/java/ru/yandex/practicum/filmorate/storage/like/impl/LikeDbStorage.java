package ru.yandex.practicum.filmorate.storage.like.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Component
@Primary
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage, EventStorage eventStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        User user = userStorage.findUser(userId);
        String sqlQuery = "merge into LIKES (FILM_ID, USER_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, user.getId());
        eventStorage.addEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        User user = userStorage.findUser(userId);
        String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, user.getId());
        eventStorage.addEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    @Override
    public Set<Long> getLikes(Long filmId) {
        String sql = "select USER_ID from LIKES where FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("USER_ID"), filmId));
    }


}
