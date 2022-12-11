package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorageTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest extends UserStorageTest<UserDbStorage> {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final LocalDate birthday = LocalDate.of(2000, 12, 12);

    User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("test@email.com")
                .login("testLogin")
                .name("testName")
                .birthday(birthday)
                .build();
    }

    @AfterEach
    void deleteData() {
        jdbcTemplate.update("drop all objects");
        jdbcTemplate.update("runscript from 'src/test/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/data.sql'");
    }

    @Test
    @DisplayName("01) Проверка получения всех юзеров из БД")
    void getUsersTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<User> users = userStorage.getUsers();
        assertEquals(10, users.size());
    }

    @Test
    @DisplayName("02) Проверка получения пустого списка юзеров из БД")
    void getUsersTes2() {
        List<User> users = userStorage.getUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("03) Проверка добавления нового пользователя в БД")
    void addUserTest() {
        User addedUser = userStorage.addNewUser(user);
        List<User> users = userStorage.getUsers();
        assertAll(
                () -> assertEquals(1, users.size()),
                () -> assertEquals(1L, addedUser.getId()),
                () -> assertEquals("test@email.com", addedUser.getEmail()),
                () -> assertEquals("testLogin", addedUser.getLogin()),
                () -> assertEquals("testName", addedUser.getName()),
                () -> assertEquals(birthday, addedUser.getBirthday())
        );
    }

    @Test
    @DisplayName("04) Проверка обновления фильма")
    void updateUserTest() {
        userStorage.addNewUser(user);
        LocalDate newBirthday = LocalDate.of(1999, 12, 12);
        User updatedUser = User.builder()
                .id(1L)
                .email("new@email.com")
                .login("newLogin")
                .name("newName")
                .birthday(newBirthday)
                .build();
        User savedUpdatedUser = userStorage.updateUser(updatedUser);
        List<User> users = userStorage.getUsers();
        assertAll(
                () -> assertEquals(1, users.size()),
                () -> assertEquals(1L, savedUpdatedUser.getId()),
                () -> assertEquals("new@email.com", savedUpdatedUser.getEmail()),
                () -> assertEquals("newLogin", savedUpdatedUser.getLogin()),
                () -> assertEquals("newName", savedUpdatedUser.getName()),
                () -> assertEquals(newBirthday, savedUpdatedUser.getBirthday())
        );
    }

    @Test
    @DisplayName("05) Проверка получения ошибки при обновлении несуществующего пользователя")
    void updateUserTest2() {
        user.setId(1L);
        assertThrows(
                EntityNotFoundException.class,
                () -> userStorage.updateUser(user)
        );
    }

    @Test
    @DisplayName("06) Провервка получения пользователя по ID")
    void findUserTest() {
        userStorage.addNewUser(user);
        User findUser = userStorage.findUser(1L);
        assertAll(
                () -> assertEquals(1L, findUser.getId()),
                () -> assertEquals("test@email.com", findUser.getEmail()),
                () -> assertEquals("testLogin", findUser.getLogin()),
                () -> assertEquals("testName", findUser.getName()),
                () -> assertEquals(birthday, findUser.getBirthday())
        );
    }

    @Test
    @DisplayName("07) Проверка получения пользователя по несуществующему ID")
    void findUserTest2() {
        assertThrows(
                EntityNotFoundException.class,
                () -> userStorage.findUser(1L)
        );
    }

    @Test
    @DisplayName("08) Проверка добавления в друзья")
    void addFriendTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        userStorage.addFriend(6L, 7L);
        userStorage.addFriend(6L, 8L);
        List<User> friends = userStorage.getUserFriends(6L);
        assertEquals(2, friends.size());
        assertEquals(7L, friends.get(0).getId());
        assertEquals(8L, friends.get(1).getId());
    }

    @Test
    @DisplayName("09) Проверка получения ошибки при добавлении в друзья несуществующего пользователя")
    void addFriendTest2() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> userStorage.addFriend(6L, 11L)
        );
    }

    @Test
    @DisplayName("10) Проверка получения ошибки при добавлении в друзья несуществующему пользователю")
    void addFriendTest3() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> userStorage.addFriend(11L, 6L)
        );
    }

    @Test
    @DisplayName("11) Проверка получения ошибки при добавлении в друзья самого себя")
    void addFriendTest4() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                BadRequestException.class,
                () -> userStorage.addFriend(6L, 6L)
        );
    }

    @Test
    @DisplayName("12) Проверка удаления из друзей")
    void deleteFriendTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<User> frineds = userStorage.getUserFriends(1L);
        assertAll(
                () -> assertEquals(1, frineds.size()),
                () -> assertEquals(2L, frineds.get(0).getId())
        );
        userStorage.deleteFriend(1L, 2L);
        List<User> noFriends = userStorage.getUserFriends(1L);
        assertTrue(noFriends.isEmpty());
    }

    @Test
    @DisplayName("13) Проверка получения ошибки при удалении из друзей самого себя")
    void deleteFriendTest2() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                BadRequestException.class,
                () -> userStorage.deleteFriend(6L, 6L)
        );
    }

    @Test
    @DisplayName("14) Проверка получения ошибки при удалении из друзей несуществующего пользователя")
    void deleteFriendTest3() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> userStorage.deleteFriend(6L, 11L)
        );
    }

    @Test
    @DisplayName("15) Проверка получения ошибки при удалении друга у несуществующего пользователя")
    void deleteFriendTest4() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        assertThrows(
                EntityNotFoundException.class,
                () -> userStorage.deleteFriend(11L, 6L)
        );
    }

    @Test
    @DisplayName("16) Проверка получения пустого списка друзей пользователя")
    void getUserFriendsTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<User> friends = userStorage.getUserFriends(6L);
        assertTrue(friends.isEmpty());
    }

    @Test
    @DisplayName("17) Проверка получения списка друзей пользователя")
    void getUserFriendsTest2() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<User> friends = userStorage.getUserFriends(1L);
        User friend = userStorage.findUser(2L);
        assertAll(
                () -> assertEquals(1, friends.size()),
                () -> assertEquals(List.of(friend), friends)
        );
    }

    @Test
    @DisplayName("18) Проверка получения пустого списка общих друзей")
    void getCommonFriendsTest() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        List<User> commonFriends = userStorage.getCommonFriends(1L, 5L);
        assertTrue(commonFriends.isEmpty());
    }

    @Test
    @DisplayName("19) Проверка получения списка общих друзей")
    void getCommonFriendsTest2() {
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        User commonFriend = userStorage.findUser(1L);
        List<User> commonFriends = userStorage.getCommonFriends(2L, 3L);
        assertAll(
                () -> assertEquals(1, commonFriends.size()),
                () -> assertEquals(List.of(commonFriend), commonFriends)
        );
    }

    @Test
    @DisplayName("20) Проверка удаления пользователя")
    void deleteFilmTest(){
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");
        Integer listUsersBeforeDelete = userStorage.getUsers().size();
        userStorage.deleteUser(1L);
        Integer listUsersAfterDelete = userStorage.getUsers().size();
        assertNotEquals(listUsersBeforeDelete, listUsersAfterDelete);
        assertEquals(userStorage.getUsers().size(), 9);
    }

    @Test
    @DisplayName("21) Проверка получения ошибки при получении ленты событий несуществующего пользователя")
    void getFeedTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> userStorage.getFeed(1L)
        );
    }
}
