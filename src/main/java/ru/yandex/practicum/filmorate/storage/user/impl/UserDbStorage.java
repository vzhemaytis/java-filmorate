package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FriendStorage friendStorage;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendStorage friendStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendStorage = friendStorage;
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from USERS order by USER_ID";
        List<User> users =  jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        users.forEach(user -> user.setFriends(getFriendsIds(user.getId())));
        return users;
    }

    @Override
    public User addNewUser(User user) {
        String sqlQuery = "insert into USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findUser(userId);
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update USERS set " +
                "EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? " +
                "where USER_ID = ?";
        jdbcTemplate.update(sqlQuery
        , user.getEmail()
        , user.getLogin()
        , user.getName()
        , user.getBirthday()
        , user.getId());
        return findUser(user.getId());
    }

    @Override
    public User findUser(Long id) {
        try {
            String sql = "select * from USERS where USER_ID = ?";
            User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
            user.setFriends(getFriendsIds(user.getId()));
            return user;
        } catch (DataAccessException ex) {
            throw new EntityNotFoundException(String.format("%s with id= %s not found", User.class, id));
        }
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        User user = findUser(id);
        User friend = findUser(friendId);
        friendStorage.addFriend(user.getId(), friend.getId());
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        User user = findUser(id);
        User friend = findUser(friendId);
        friendStorage.deleteFriend(user.getId(), friend.getId());
    }

    @Override
    public List<User> getUserFriends(Long id) {
        String sql = "select * from USERS as U " +
                "inner join (select FRIEND_ID from FRIENDS where USER_ID = ?) as F on U.USER_ID = F.FRIEND_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sql = "select * from USERS as U " +
                "inner join (select FRIEND_ID from FRIENDS where USER_ID = ?) as F on U.USER_ID = F.FRIEND_ID " +
                "inner join (select FRIEND_ID from FRIENDS where USER_ID = ?) as O on U.USER_ID = O.FRIEND_ID ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, otherId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("USER_ID"))
                .email(rs.getString("EMAIL"))
                .login(rs.getString("LOGIN"))
                .name(rs.getString("USER_NAME"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .build();
    }

    private Set<Long> getFriendsIds(Long id) {
        return getUserFriends(id).stream().map(User::getId).collect(Collectors.toSet());
    }
}
