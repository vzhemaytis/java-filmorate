package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    long addReview(Review review);

    Review getReviewById(Long id);

    void deleteReviewById(Long id);

    void updateReviewById(Review review);

    List<Review> getReviews(Long filmId, int count);

    public void addReactionToReview(Long id, Long userId, boolean isPositive);

    void deleteLikeOrDislikeToReview(Long id, Long userId);
}