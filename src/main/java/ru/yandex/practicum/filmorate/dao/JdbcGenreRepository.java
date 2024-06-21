package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.GenreExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.GenresForFilmExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.GenresForFilmsExtractor;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;


    @Override
    public Optional<Genre> getGenreById(int genreId) {
        String sql = "SELECT GENRE_ID, NAME FROM GENRES WHERE GENRE_ID = :genre_id";
        Map<String, Object> param = Map.of("genre_id", genreId);
        return Optional.ofNullable(jdbc.query(sql, param, new GenreExtractor()));
    }

    @Override
    public Set<Genre> getGenresById(List<Integer> genresId) {
        String sql = "SELECT GENRE_ID, NAME FROM GENRES WHERE GENRE_ID IN ( :genres_id )";
        Map<String, Object> param = Map.of("genres_id", genresId);
        return jdbc.query(sql, param, new GenresForFilmExtractor());
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT GENRE_ID AS ID, NAME FROM GENRES;";
        return jdbc.query(sql, new GenreRowMapper());
    }

    @Override
    public Set<Genre> getGenresByFilm(int filmId) {
        String sql = """
                SELECT  FILM_GENRES.GENRE_ID AS GENRE_ID,
                       NAME
                FROM FILM_GENRES
                JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                WHERE FILM_ID = :film_id
                """;
        Map<String, Object> param = Map.of("film_id", filmId);
        return jdbc.query(sql, param, new GenresForFilmExtractor());
    }

    @Override
    public Map<Integer, Set<Genre>> getGenresByFilms(Collection<Integer> filmsId) {
        String sql = """
                SELECT FILM_ID,
                       FILM_GENRES.GENRE_ID AS GENRE_ID,
                       NAME
                FROM FILM_GENRES
                    LEFT JOIN GENRES ON GENRES.GENRE_ID = FILM_GENRES.GENRE_ID
                WHERE FILM_ID IN (:films_id);
                """;
        Map<String, Object> param = Map.of("films_id", filmsId);

        return jdbc.query(sql, param, new GenresForFilmsExtractor());
    }
}
