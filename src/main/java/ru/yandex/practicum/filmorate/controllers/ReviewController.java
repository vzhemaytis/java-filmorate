package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        long reviewId = reviewService.addReview(review);
        log.info("created review = {}", review);
        return reviewService.getReviewById(reviewId);
    }

    @PutMapping
    public Review updateReviewById(@Valid @RequestBody Review review) {
        log.info("updated review = {}", review);
        return reviewService.updateReview(review);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("user with id = {} liked review with id = {}", userId, id);
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("user with id = {} disliked review with id = {}", userId, id);
        reviewService.addDislikeToReview(id, userId);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        log.info("got request for review with id = {}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(defaultValue = "10") int count) {
        log.info("got request for review list for filmID = {}, limit = {}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {
        log.info("got delete request for review  id = {}", id);
        reviewService.deleteReviewById(id);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeByReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("got delete like request for review  id = {} from user with = {}", id, userId);
        reviewService.deleteLikeOrDislikeToReview(id, userId);
    }

    @DeleteMapping("{reviewId}/dislike/{userId}")
    public void deleteDislikeByReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("got delete dislike request for review  id = {} from user with = {}", reviewId, userId);
        reviewService.deleteLikeOrDislikeToReview(reviewId, userId);
    }


}