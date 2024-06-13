package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@Import(JdbcFilmRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmRepositoryTests {
    private final JdbcFilmRepository filmRepository;

    @Test
    public void saveFilm() {
        Film newFilm = new Film(
                "New Film",
                "description",
                LocalDate.now(),
                100
        );

        newFilm.setMpa(new Mpa(1, null));

        Optional<Film> filmOptional = Optional.ofNullable(filmRepository.save(newFilm));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                   assertThat(film).hasFieldOrPropertyWithValue("name", "New Film")
                );
    }

    @Test
    public void getById() {
        Optional<Film> filmOptional = filmRepository.getById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", "film1"));
    }

    @Test
    public void updateFilm() {
         Film film = new Film("newFilm", "description", LocalDate.now(), 100);
         film.setMpa(new Mpa(1, null));
         film.setId(1);
         filmRepository.update(film);

         Optional<Film> optionalFilm = filmRepository.getById(1);

         assertThat(optionalFilm)
                 .isPresent()
                 .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("name", "newFilm"));
    }

    @Test
    public void delete() {
        filmRepository.delete(1);

        Optional<Film> filmOptional = filmRepository.getById(1);

        assertThat(filmOptional).isEmpty();
    }
/*
    @Test
    public void getAll() {
        List<Film> films = filmRepository.getAll();

        assertThat(films.size())
                .isEqualTo(6);
    }

    @Test
    public void getTopPopular() {
        List<Film> films = filmRepository.getTopPopular(1000);


        assertThat(films.size())
                .isEqualTo(6);
        assertThat(films.get(0))
                .hasFieldOrPropertyWithValue("name", "film3");
        assertThat(films.get(1))
                .hasFieldOrPropertyWithValue("name", "film4");
    }*/

    @Test
    public void getFilmsByGenre() {
        List<Film> films = filmRepository.getFilmsByGenre(2);

        assertThat(films.size()).isEqualTo(3);
    }

    @Test
    public void getFilmsByRating() {
        List<Film> films = filmRepository.getFilmsByRating(1);

        assertThat(films.size()).isEqualTo(2);
    }
}
