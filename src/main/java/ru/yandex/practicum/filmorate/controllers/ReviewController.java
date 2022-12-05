package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        long reviewId = reviewService.addReview(review);
        return reviewService.getReviewById(reviewId);
    }

    @PutMapping
    public Review updateReviewById(@Valid @RequestBody Review review) {
        return reviewService.updateReviewById(review);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislikeToReview(id, userId);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviews(filmId, count);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeByReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteLikeOrDislikeToReview(id, userId);
    }

    @DeleteMapping("{reviewId}/dislike/{userId}")
    public void deleteDislikeByReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.deleteLikeOrDislikeToReview(reviewId, userId);
    }


}