package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public UserService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addNewUser(User user) {
        return userStorage.addNewUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User findUser(Long id) {
        return userStorage.findUser(id);
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getUserFriends(Long id) {
        return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    public List<Film> getRecommendationsFilms(Long id) {
        List<Long> likeFilm = filmStorage.getLikeFilmsUsersId(id);
        List<Long> recommendations = filmStorage.getRecommendations(id);
        recommendations.removeAll(likeFilm);
        List<Film> recommendationUserFilms = new ArrayList<>();
        for (Long filmId : recommendations) {
            recommendationUserFilms.add(filmStorage.findFilm(filmId));
        }
        return recommendationUserFilms;
    }
}
