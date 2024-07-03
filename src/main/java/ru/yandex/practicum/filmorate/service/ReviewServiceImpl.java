package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.ReviewRepository;
import ru.yandex.practicum.filmorate.dao.ReviewUsefulRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewUsefulRepository usefulRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final EventService eventService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewUsefulRepository usefulRepository, UserRepository userRepository, FilmRepository filmRepository, EventService eventService) {
        this.reviewRepository = reviewRepository;
        this.usefulRepository = usefulRepository;
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
        this.eventService = eventService;
    }

    @Override
    public Review saveReview(Review review) {
        filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException(String.format("Фильма с id %s нет в базе", review.getFilmId())));
        userRepository.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", review.getUserId())));

        Review review1 = reviewRepository.save(review);
        eventService.addEvent(new Event(review1.getUserId(), EventType.REVIEW.name(), Operation.ADD.name(),
                review1.getId()));
        return review1;
    }

    @Override
    public Review updateReview(Review review) {
        filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException(String.format("Фильма с id %s нет в базе", review.getFilmId())));
        userRepository.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", review.getUserId())));
        reviewRepository.getById(review.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Отзыва с id %s нет в базе", review.getId())));
        reviewRepository.update(review);
        Review updateReview = reviewRepository.getById(review.getId()).orElseThrow();
        eventService.addEvent(new Event(updateReview.getUserId(), EventType.REVIEW.name(), Operation.UPDATE.name(),
                updateReview.getId()));
        return updateReview;
    }

    @Override
    public void deleteReview(int reviewId) {
        Review review = reviewRepository.getById(reviewId).orElseThrow(() ->
                new NotFoundException(String.format("Отзыва с id %s нет в базе", reviewId)));
        reviewRepository.delete(reviewId);

        eventService.addEvent(new Event(review.getUserId(), EventType.REVIEW.name(), Operation.REMOVE.name(),
                reviewId));
    }

    @Override
    public Review getReview(Integer reviewId) {
        return reviewRepository.getById(reviewId)
                .orElseThrow(() -> new NotFoundException(String.format("Отзыва с id %s нет в базе", reviewId)));
    }

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        if (filmId != null) {
            filmRepository.getById(filmId)
                    .orElseThrow(() -> new NotFoundException(String.format("Фильма с id %s нет в базе", filmId)));
        }

        return filmId == null
                ? reviewRepository.getReviews(count)
                : reviewRepository.getReviewsByFilm(filmId, count);
    }

    @Override
    public void addMarkToReview(int reviewId, int userId, boolean isLike) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", userId)));
        reviewRepository.getById(reviewId)
                .orElseThrow(() -> new NotFoundException(String.format("Отзыва с id %s нет в базе", reviewId)));

        if (isLike) {
            usefulRepository.addLike(reviewId, userId);
        } else {
            usefulRepository.addDislike(reviewId, userId);
        }
    }

    @Override
    public void deleteMarkToReview(int reviewId, int userId, boolean isLike) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %s нет в базе", userId)));
        reviewRepository.getById(reviewId)
                .orElseThrow(() -> new NotFoundException(String.format("Отзыва с id %s нет в базе", reviewId)));

        if (isLike) {
            usefulRepository.deleteLike(reviewId, userId);
        } else {
            usefulRepository.deleteDislike(reviewId, userId);
        }
    }
}
