package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@Import(JdbcFilmRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmRepositoryTests {
    private final JdbcFilmRepository filmRepository;

    @Test
    public void saveFilm() {
        LocalDate date = LocalDate.now();
        Film newFilm = new Film(
                "New Film",
                "description",
                date,
                100
        );

        newFilm.setMpa(new Mpa(1, null));

        Film film = filmRepository.save(newFilm);
        assertThat(film).hasFieldOrPropertyWithValue("name", "New Film");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description" );
        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", date);
        assertThat(film).hasFieldOrPropertyWithValue("duration", 100);
    }

    @Test
    public void getById() {
        Optional<Film> filmOptional = filmRepository.getById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("name", "film1");
                    assertThat(film).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(film).hasFieldOrPropertyWithValue("description", "description");
                    assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2010, 10, 10));
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 100);
                    assertThat(film).hasFieldOrPropertyWithValue("mpa", new Mpa(1, "G"));
                });
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
                 .hasValueSatisfying(f -> {
                     assertThat(f).hasFieldOrPropertyWithValue("name", "newFilm");
                     assertThat(film).hasFieldOrPropertyWithValue("id", 1);
                     assertThat(film).hasFieldOrPropertyWithValue("description", "description");
                     assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2010, 10, 10));
                     assertThat(film).hasFieldOrPropertyWithValue("duration", 100);
                     assertThat(film).hasFieldOrPropertyWithValue("mpa", new Mpa(1, "G"));
                 });
    }

    @Test
    public void delete() {
        filmRepository.delete(1);

        Optional<Film> filmOptional = filmRepository.getById(1);

        assertThat(filmOptional).isEmpty();
    }
}
