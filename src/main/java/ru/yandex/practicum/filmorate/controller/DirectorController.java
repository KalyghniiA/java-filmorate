package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping(value = "/directors")
    public List<Director> getDirectors() {
        log.info("Получен запрос на получение списка режиссеров");
        List<Director> directors = directorService.getDirectors();
        log.info("Отправлен список режиссеров");
        return directors;
    }

    @GetMapping(value = "/directors/{id}")
    public Director getDirectorById(@PathVariable("id") Integer id) {
        log.info(String.format("Получен GET запрос на получение режиссера с id %s", id));
        Director director = directorService.getDirectorById(id);
        log.info(String.format("Режиссер с id %s отправлен", id));
        return director;
    }

    @PostMapping(value = "/directors")
    public Director createDirector(@RequestBody Director director) {
        log.info("Получен запрос на добавление режиссера");
        Director newDirector = directorService.saveDirector(director);
        log.info(String.format("Новый директор сохранен с id %s", newDirector.getId()));
        return newDirector;
    }

    @PutMapping(value = "/directors")
    public Director updateDirector(@RequestBody Director director) {
        log.info(String.format("Получен запрос на обновление режиссера с id %s", director.getId()));
        Director newDirector = directorService.updateDirector(director);
        log.info(String.format("Директор с id %s обновлен", newDirector.getId()));
        return newDirector;
    }

    @DeleteMapping(value = "/directors/{id}")
    public void deleteDirector(@PathVariable("id") Integer id) {
        log.info(String.format("Получен запрос на удаление директора под id %s", id));
        directorService.deleteDirector(id);
        log.info(String.format("Директор с id %s удален", id));
    }
}
