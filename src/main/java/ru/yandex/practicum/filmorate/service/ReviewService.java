package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;


@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(@Qualifier("ReviewDbStorage") ReviewStorage reviewStorage,
                         UserStorage userStorage,
                         FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }


    public long addReview(Review review) {
        userStorage.findUser(review.getUserId());
        filmStorage.findFilm(review.getFilmId());

        return reviewStorage.addReview(review);
    }

    public Review getReviewById(long reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public void checkIfReviewExists(Long reviewId) {
        getReviewById(reviewId);
    }

    public void deleteReviewById(Long reviewId) {
        checkIfReviewExists(reviewId);
        reviewStorage.deleteReviewById(reviewId);
    }

    public Review updateReview(Review review) {
        userStorage.findUser(review.getUserId());
        filmStorage.findFilm(review.getFilmId());
        reviewStorage.updateReviewById(review);
        return reviewStorage.getReviewById(review.getReviewId());
    }

    public List<Review> getReviews(Long filmId, int count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public void addLikeToReview(Long id, Long userId) {
        checkIfReviewExists(id);
        userStorage.findUser(userId);
        reviewStorage.addReactionToReview(id, userId, true);
    }

    public void addDislikeToReview(Long reviewId, Long userId) {
        checkIfReviewExists(reviewId);
        userStorage.findUser(userId);
        reviewStorage.addReactionToReview(reviewId, userId, false);
    }

    public void deleteLikeOrDislikeToReview(Long reviewId, Long userId) {
        checkIfReviewExists(reviewId);
        userStorage.findUser(userId);
        reviewStorage.deleteLikeOrDislikeToReview(reviewId, userId);
    }

}