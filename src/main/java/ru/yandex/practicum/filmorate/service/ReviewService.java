package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
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
        validateReview(review);
        return reviewStorage.addReview(review);
    }

    public Review getReviewById(long reviewId) {
        try {
            return reviewStorage.getReviewById(reviewId);
        } catch (DataAccessException e) {
            throw new EntityNotFoundException(String.format("Ревью с id = %d не найдено", reviewId));
        }
    }

    public void checkIfReviewExists(Long reviewId) {
        getReviewById(reviewId);
    }

    public void deleteReviewById(Long reviewId) {
        checkIfReviewExists(reviewId);
        reviewStorage.deleteReviewById(reviewId);
    }

    public Review updateReviewById(Review review) {
        validateReview(review);
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

    public void validateReview(Review review) {
        Long reviewId = review.getReviewId();
        if (reviewId != null) {
            checkIfReviewExists(reviewId);
        }

        Long userId = review.getUserId();
        if (userId == null) {
            throw new RuntimeException("userId cannot be empty");
        }
        userStorage.findUser(userId);

        Long filmId = review.getFilmId();
        if (filmId == null) {
            throw new RuntimeException("userId cannot be empty");
        }
        filmStorage.findFilm(filmId);

        String content = review.getContent();
        if (content == null) {
            throw new RuntimeException("userId cannot be empty");
        }

        Boolean isPositive = review.getIsPositive();
        if (isPositive == null) {
            throw new RuntimeException("userId cannot be empty");
        }
    }

}