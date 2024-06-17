package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaRepository mpaRepository;

    @Override
    public List<Mpa> getRatings() {
        return mpaRepository.getRatings();
    }

    @Override
    public Mpa getRatingById(int ratingId) {
        return mpaRepository.getRatingById(ratingId).orElseThrow(() -> new NotFoundException(String.format("Рейтинга с id %s нет в базе", ratingId)));
    }
}
