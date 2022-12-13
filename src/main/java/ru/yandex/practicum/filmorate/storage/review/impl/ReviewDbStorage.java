package ru.yandex.practicum.filmorate.storage.review.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Component("ReviewDbStorage")
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;


    public ReviewDbStorage(JdbcTemplate jdbcTemplate, EventStorage eventStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventStorage = eventStorage;
    }

    public long addReview(Review review) {
        String sqlQuery = "INSERT INTO reviews (user_id, film_id, content, is_positive) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            ps.setLong(1, review.getUserId());
            ps.setLong(2, review.getFilmId());
            ps.setString(3, review.getContent());
            ps.setBoolean(4, review.getIsPositive());
            return ps;
        }, keyHolder);
        eventStorage.addEvent(review.getUserId(), EventType.REVIEW, Operation.ADD, keyHolder.getKey().longValue());
        return keyHolder.getKey().longValue();

    }

    public Review getReviewById(Long id) {
        String sqlQuery = "SELECT r.review_id, r.film_id, r.user_id, r.content, r.is_positive," +
                "COUNT(postive_r.review_id) - COUNT(negative_r.review_id) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN (SELECT review_id FROM reviews_reactions WHERE is_positive = TRUE) AS postive_r ON r.review_id = postive_r.review_id " +
                "LEFT JOIN (SELECT review_id FROM reviews_reactions WHERE is_positive = FALSE) AS negative_r ON r.review_id = negative_r.review_id " +
                "WHERE r.review_id = ? " +
                "GROUP BY r.review_id, r.film_id, r.user_id, r.content, r.is_positive;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeReview, id);
        } catch (DataAccessException e) {
            throw new EntityNotFoundException(String.format("Ревью с id = %d не найдено", id));
        }
    }

    public void updateReviewById(Review review) {
        Review oldReview = getReviewById(review.getReviewId());
        eventStorage.addEvent(oldReview.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getReviewId());
        String sqlQuery = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getReviewId());
            return ps;
        });
    }

    public void deleteReviewById(Long reviewId) {
        Review review = getReviewById(reviewId);
        eventStorage.addEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, reviewId);
        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setLong(1, reviewId);
            return ps;
        });
    }

    public List<Review> getReviews(Long filmId, int count) {
        String sqlQuery = "SELECT r.review_id, r.film_id, r.user_id, r.content, r.is_positive," +
                "COUNT(postive_r.review_id) - COUNT(negative_r.review_id) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN (SELECT review_id FROM reviews_reactions WHERE is_positive = TRUE) " +
                "AS postive_r ON r.review_id = postive_r.review_id " +
                "LEFT JOIN (SELECT review_id FROM reviews_reactions WHERE is_positive = FALSE) " +
                "AS negative_r ON r.review_id = negative_r.review_id ";


        if (filmId != null) {
            // не под prepare statement, но ожидаем, что оно привелось к long, так что не так страшно, наверное
            sqlQuery = sqlQuery + " WHERE film_id = " + filmId;
        }

        sqlQuery = sqlQuery + "GROUP BY r.review_id, r.film_id, r.user_id, r.content, r.is_positive ORDER BY useful DESC " +
                "LIMIT ?;";

        String finalSqlQuery = sqlQuery;
        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(finalSqlQuery);
            ps.setInt(1, count);
            return ps;
        }, this::makeReview);
    }

    public void addReactionToReview(Long id, Long userId, boolean isPositive) {
        String sqlQuery = "INSERT INTO reviews_reactions (review_id, user_id, is_positive) values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId, isPositive);
    }

    public void deleteLikeOrDislikeToReview(Long reviewId, Long userId) {
        String sqlQuery = "DELETE FROM reviews_reactions WHERE review_id = ? and user_id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setLong(1, reviewId);
            ps.setLong(2, userId);
            return ps;
        });
    }

    private Review makeReview(ResultSet resultSet, long rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .useful(resultSet.getLong("useful"))
                .build();
    }
}
