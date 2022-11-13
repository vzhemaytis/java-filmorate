package ru.yandex.practicum.filmorate.storage.friend.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

@Component
@Primary
@Slf4j
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "insert into FRIENDS (USER_ID, FRIEND_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.info("Юзер с id: {} добавлен в друзья к юзеру с id: {}",friendId, id);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sqlQuery = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.info("Юзер с id: {} удален из друзей юзера с id: {}",friendId, id);
    }
}
