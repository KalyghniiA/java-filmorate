package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@Import(JdbcMpaRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaRepositoryTest {
    private final JdbcMpaRepository mpaRepository;

    @Test
    public void getRatingById() {
        Optional<Mpa> optionalMpa = mpaRepository.getRatingById(1);

        assertThat(optionalMpa)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
                });
    }

    @Test
    public void getRatings() {
        Optional<List<Mpa>> optionalMpaList = Optional.ofNullable(mpaRepository.getRatings());

        assertThat(optionalMpaList)
                .isPresent();
    }
}
