package ru.yandex.practicum.filmorate.storage.like.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorageTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeDbStorageTest extends LikeStorageTest<LikeDbStorage> {

    private final JdbcTemplate jdbcTemplate;
    private final LikeStorage likeStorage;

    @AfterEach
    void deleteData() {
        jdbcTemplate.update("drop all objects");
        jdbcTemplate.update("runscript from 'src/test/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/data.sql'");
    }

    @Test
    @DisplayName("1) Проверка добавления и получения списка лайков")
    void addLikeAndGetLikesAfterAddTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        Set<Long> likes = likeStorage.getLikes(5L);
        assertTrue(likes.isEmpty());
        likeStorage.addLike(5L, 1L);
        Set<Long> newLikes = likeStorage.getLikes(5L);
        assertAll(
                () -> assertEquals(1, newLikes.size()),
                () -> assertEquals(Set.of(1L), newLikes)
        );
    }

    @Test
    @DisplayName("2) Проверка удаления и получения списка лайков")
    void deleteLikeAndGetLikesAfterDeleteTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        Set<Long> likes = likeStorage.getLikes(1L);
        assertAll(
                () -> assertEquals(3, likes.size()),
                () -> assertEquals(Set.of(1L, 2L, 3L), likes)
        );
        likeStorage.deleteLike(1L, 1L);
        Set<Long> newLikes = likeStorage.getLikes(1L);
        assertAll(
                () -> assertEquals(2, newLikes.size()),
                () -> assertEquals(Set.of(2L, 3L), newLikes)
        );
    }

    @Test
    @DisplayName("3) Проверка получения пустого списка лайков")
    void getEmptyLikesListTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        Set<Long> likes = likeStorage.getLikes(5L);
        assertTrue(likes.isEmpty());    }
}
