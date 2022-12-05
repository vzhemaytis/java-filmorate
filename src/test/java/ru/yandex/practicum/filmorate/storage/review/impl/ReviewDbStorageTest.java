package ru.yandex.practicum.filmorate.storage.review.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorageTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest extends ReviewStorageTest<ReviewDbStorage> {
    private final ReviewStorage reviewStorage;
    private final JdbcTemplate jdbcTemplate;
    Review review;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("drop all objects");
        jdbcTemplate.update("runscript from 'src/test/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/data.sql'");
        jdbcTemplate.update("runscript from 'src/test/resources/testdata.sql'");

    }

    @Test
    @DisplayName("Получение ревью фильма по айди")
    void getReviewByIdTest() {
        long userId = 1L;
        long filmId = 1L;
        long reviewId = 1L;

        Review review = reviewStorage.getReviewById(reviewId);
        assertEquals(review.getReviewId(), reviewId);
        assertEquals(review.getUserId(), userId);
        assertEquals(review.getFilmId(), filmId);
        assertEquals(review.getContent(), "Ревью фильма 1 пользователем 1");
        assertEquals(review.getIsPositive(), true);
    }

    @Test
    @DisplayName("Получение ревью фильма по айди")
    void addReviewTest() {
        long idToCreate = 14L;
        long userId = 1L;
        long filmId = 10L;

        assertThrows(EmptyResultDataAccessException.class, () -> {
            reviewStorage.getReviewById(idToCreate);
        });
        review = Review.builder()
                .userId(userId)
                .filmId(filmId)
                .content("Ревью фильма 10")
                .isPositive(true)
                .build();

        long reviewId = reviewStorage.addReview(review);

        Review newReview = reviewStorage.getReviewById(idToCreate);
        assertEquals(reviewId, newReview.getReviewId());
        assertEquals(review.getUserId(), newReview.getUserId());
        assertEquals(review.getFilmId(), newReview.getFilmId());
        assertEquals(review.getContent(), newReview.getContent());
        assertEquals(review.getIsPositive(), newReview.getIsPositive());
    }

    @Test
    @DisplayName("Удаление ревью по id")
    void deleteReviewByIdTest() {
        long idToDelete = 13L;

        Review review = reviewStorage.getReviewById(idToDelete);
        assertEquals(review.getReviewId(), idToDelete);
        reviewStorage.deleteReviewById(idToDelete);
        assertThrows(EmptyResultDataAccessException.class, () -> {
            reviewStorage.getReviewById(idToDelete);
        });
    }


    @Test
    @DisplayName("Получение всех ревью для фильма")
    void getFilmReviewsTest() {
        long filmId = 1L;

        List<Review> reviews = reviewStorage.getReviews(filmId, 10);
        assertEquals(3, reviews.size());
    }

    @Test
    @DisplayName("Обновление ревью фильма")
    void updateReviewByIdTest() {
        long reviewId = 1L;
        long userId = 1L;
        long filmId = 1L;

        Review oldReviewData = reviewStorage.getReviewById(reviewId);
        Review newReviewData = Review.builder()
                .reviewId(oldReviewData.getReviewId())
                .userId(userId)
                .filmId(filmId)
                .content("Обновленное ревью фильма 10 ")
                .isPositive(false)
                .build();

        reviewStorage.updateReviewById(newReviewData);

        Review updatedReview = reviewStorage.getReviewById(reviewId);

        assertEquals(reviewId, updatedReview.getReviewId());
        assertEquals(newReviewData.getUserId(), updatedReview.getUserId());
        assertEquals(newReviewData.getFilmId(), updatedReview.getFilmId());
        assertEquals(newReviewData.getContent(), updatedReview.getContent());
        assertEquals(newReviewData.getIsPositive(), updatedReview.getIsPositive());
    }

    @Test
    @DisplayName("Обновление ревью фильма")
    void addReactionToReviewTest() {

        long reviewId = 1L;
        long userId = 5L;

        // проверили, что useful = 4
        Review review = reviewStorage.getReviewById(reviewId);
        assertEquals(reviewId, review.getReviewId());
        assertEquals(4, review.getUseful());

        // поставили лайк
        reviewStorage.addReactionToReview(reviewId, userId, true);

        // проверили, что usefult = 5
        Review updatedReview = reviewStorage.getReviewById(reviewId);
        assertEquals(reviewId, updatedReview.getReviewId());
        assertEquals(5, updatedReview.getUseful());
    }

    @Test
    @DisplayName("Обновление ревью фильма")
    void removeReactionToReviewTest() {
        long reviewId = 1L;
        long userId = 1L;

        // проверили, что useful = 4
        Review review = reviewStorage.getReviewById(reviewId);
        assertEquals(reviewId, review.getReviewId());
        assertEquals(4, review.getUseful());

        // поставили лайк
        reviewStorage.deleteLikeOrDislikeToReview(reviewId, userId);

        // проверили, что usefult = 5
        Review updatedReview = reviewStorage.getReviewById(reviewId);
        assertEquals(reviewId, updatedReview.getReviewId());
        assertEquals(3, updatedReview.getUseful());
    }
}
