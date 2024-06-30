package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        return directorRepository.get();
    }

    @Override
    public Director getDirectorById(int directorId) {
        return directorRepository
                .getById(directorId)
                .orElseThrow(() -> new NotFoundException(String.format("Режиссера с id %s нет в базе", directorId)));
    }

    @Override
    public Director saveDirector(Director director) {
        return directorRepository.create(director);
    }

    @Override
    public Director updateDirector(Director director) {
        directorRepository
                .getById(director.getId())
                .orElseThrow(() -> new NotFoundException("Режиссера с id %s нет в базе"));
        directorRepository.update(director);
        return director;
    }

    @Override
    public void deleteDirector(int directorId) {
         directorRepository.delete(directorId);
    }
}
