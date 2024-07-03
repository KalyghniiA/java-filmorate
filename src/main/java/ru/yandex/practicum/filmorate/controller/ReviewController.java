package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@RequestBody Review review) {
        log.info("Получен POST запрос на создание отзыва");
        Review newReview = reviewService.saveReview(review);
        log.info(String.format("Отзыв добавлен в базу по id %s", newReview.getId()));
        return newReview;
    }

    @PutMapping("/reviews")
    public Review updateReview(@RequestBody Review review) {
        log.info(String.format("Получен PUT запрос на изменения отзыва с id %s", review.getId()));
        Review newReview = reviewService.updateReview(review);
        log.info(String.format("Отзыв с id %s изменен", newReview.getId()));
        return newReview;
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable Integer id) {
        log.info(String.format("Получен запрос на удаление отзыва с id %s", id));
        reviewService.deleteReview(id);
        log.info("Отзыв удален");
    }

    @GetMapping("/reviews/{id}")
    public Review getReview(@PathVariable Integer id) {
        log.info(String.format("Получен запрос на получение отзыва с id %s", id));
        Review review = reviewService.getReview(id);
        log.info("Отзыв отправлен");
        return review;
    }

    @GetMapping("/reviews")
    public List<Review> getReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен запрос на получение отзывов");
        List<Review> reviews = reviewService.getReviews(filmId, count);
        log.info("Отзывы отправлены");
        return reviews;
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        log.info(String.format("Получен запрос на добавление лайка отзыву id %s от пользователя id %s", id, userId));
        reviewService.addMarkToReview(id, userId, true);
        log.info("Лайк добавлен");
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        log.info(String.format("Получен запрос на добавление дизлайка отзыву id %s от пользователя id %s", id, userId));
        reviewService.addMarkToReview(id, userId, false);
        log.info("Дизлайк добавлен");
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable int id,
            @PathVariable Integer userId) {
        log.info(String.format("Получен запрос на удаления лайка отзыву id %s от пользователя id %s", id, userId));
        reviewService.deleteMarkToReview(id, userId, true);
        log.info("Лайк удален");
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislike(
            @PathVariable int id,
            @PathVariable Integer userId) {
        log.info(String.format("Получен запрос на удаления дизлайка отзыву id %s от пользователя id %s", id, userId));
        reviewService.deleteMarkToReview(id, userId, false);
        log.info("Дизлайк удален");
    }
}
