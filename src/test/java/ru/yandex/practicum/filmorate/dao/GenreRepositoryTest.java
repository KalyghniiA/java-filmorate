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
        Optional<Genre> genreOptional = genreRepository.getGenreById(1);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
                    assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
                });
    }

    @Test
    public void getGenres() {
        List<Genre> genres = genreRepository.getGenres();
        assertThat(genres.size()).isEqualTo(6);
    }
}
