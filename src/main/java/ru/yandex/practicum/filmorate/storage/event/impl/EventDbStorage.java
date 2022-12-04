package ru.yandex.practicum.filmorate.storage.event.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        String sqlQuery = "insert into EVENTS (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, eventType.toString(), operation.toString(), entityId);
    }

    @Override
    public List<Event> getFeed(Long id) {
        String sql = "select * from EVENTS where USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), id);
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("EVENT_ID"))
                .userId(rs.getLong("USER_ID"))
                .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .entityId(rs.getLong("ENTITY_ID"))
                .timestamp(rs.getTimestamp("TIMESTAMP").toInstant())
                .build();
    }
}
