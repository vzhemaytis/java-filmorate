package ru.yandex.practicum.filmorate.storage.friend.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

@Component
@Primary
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    public FriendDbStorage(JdbcTemplate jdbcTemplate, EventStorage eventStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventStorage = eventStorage;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "merge into FRIENDS (USER_ID, FRIEND_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
        eventStorage.addEvent(id, EventType.FRIEND, Operation.ADD, friendId);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sqlQuery = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        eventStorage.addEvent(id, EventType.FRIEND, Operation.REMOVE, friendId);
    }
}
