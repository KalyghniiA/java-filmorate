package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;

    @Autowired
    public DirectorServiceImpl(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Override
    public List<Director> getDirectors() {
        return directorRepository.getDirectors();
    }

    @Override
    public Director getDirectorById(int directorId) {
        return directorRepository
                .getDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException(String.format("Режиссера с id %s нет в базе", directorId)));
    }

    @Override
    public Director saveDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Значение имя не валидно");
        }

        return directorRepository.createDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        directorRepository
                .getDirectorById(director.getId())
                .orElseThrow(() -> new NotFoundException("Режиссера с id %s нет в базе"));
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Значение имя не валидно");
        }

        directorRepository.updateDirector(director);
        return director;
    }

    @Override
    public void deleteDirector(int directorId) {
         directorRepository.deleteDirector(directorId);
    }
}
