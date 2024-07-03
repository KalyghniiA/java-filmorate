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
        Optional<Mpa> mpaOptional = mpaRepository.getById(1);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
                    assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
                });
    }

    @Test
    public void getRatings() {
        List<Mpa> mpaList = mpaRepository.getAll();

        assertThat(mpaList.size()).isEqualTo(5);
    }
}
