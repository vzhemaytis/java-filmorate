package ru.yandex.practicum.filmorate.storage.event.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorageTest;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventDbStorageTest extends EventStorageTest<EventDbStorage> {

    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;
    private final LikeStorage likeStorage;
    private final FriendStorage friendStorage;

    @AfterEach
    void deleteData() {
        jdbcTemplate.update("drop all objects");
        jdbcTemplate.update("runscript from 'src/test/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/data.sql'");
    }

    @Test
    @DisplayName("1) Получение пустой ленты событий")
    void getFeedTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<Event> feed = eventStorage.getFeed(6L);
        assertTrue(feed.isEmpty());
    }

    @Test
    @DisplayName("2) Получение ленты событий юзера 1")
    void getFeedTest2() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        likeStorage.addLike(1L, 6L);
        likeStorage.deleteLike(1L, 6L);
        friendStorage.addFriend(6L, 1L);
        friendStorage.deleteFriend(6L, 1L);
        List<Event> feed = eventStorage.getFeed(6L);
        assertEquals(4, feed.size());
        assertAll(
                () -> assertEquals(1L, feed.get(0).getEventId()),
                () -> assertEquals(6L, feed.get(0).getUserId()),
                () -> assertEquals(EventType.LIKE, feed.get(0).getEventType()),
                () -> assertEquals(Operation.ADD, feed.get(0).getOperation()),
                () -> assertEquals(1L, feed.get(0).getEntityId()),
                () -> assertTrue(feed.get(0).getTimestamp().isAfter(Instant.ofEpochMilli(1655798323570L)))
        );
        assertAll(
                () -> assertEquals(2L, feed.get(1).getEventId()),
                () -> assertEquals(6L, feed.get(1).getUserId()),
                () -> assertEquals(EventType.LIKE, feed.get(1).getEventType()),
                () -> assertEquals(Operation.REMOVE, feed.get(1).getOperation()),
                () -> assertEquals(1L, feed.get(1).getEntityId()),
                () -> assertTrue(feed.get(1).getTimestamp().isAfter(Instant.ofEpochMilli(1655798323570L)))
        );
        assertAll(
                () -> assertEquals(3L, feed.get(2).getEventId()),
                () -> assertEquals(6L, feed.get(2).getUserId()),
                () -> assertEquals(EventType.FRIEND, feed.get(2).getEventType()),
                () -> assertEquals(Operation.ADD, feed.get(2).getOperation()),
                () -> assertEquals(1L, feed.get(2).getEntityId()),
                () -> assertTrue(feed.get(2).getTimestamp().isAfter(Instant.ofEpochMilli(1655798323570L)))
        );
        assertAll(
                () -> assertEquals(4L, feed.get(3).getEventId()),
                () -> assertEquals(6L, feed.get(3).getUserId()),
                () -> assertEquals(EventType.FRIEND, feed.get(3).getEventType()),
                () -> assertEquals(Operation.REMOVE, feed.get(3).getOperation()),
                () -> assertEquals(1L, feed.get(3).getEntityId()),
                () -> assertTrue(feed.get(3).getTimestamp().isAfter(Instant.ofEpochMilli(1655798323570L)))
        );
    }


}
