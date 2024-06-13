package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@Import(JdbcGenreRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreRepositoryTest {
    private final JdbcGenreRepository genreRepository;

    @Test
    public void getGenreById() {
        Optional<Genre> optionalGenre = genreRepository.getGenreById(1);

        assertThat(optionalGenre)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void getGenres() {
        Optional<List<Genre>> optionalGenres = Optional.ofNullable(genreRepository.getGenres());
        assertThat(optionalGenres).isPresent();
    }
}
