package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Film> getById(int id) {
        String sql = """
                SELECT FILM_ID, FILMS.NAME AS NAME, DESCRIPTION, RELEASE_DATE,DURATION, GENRES.NAME AS GENRE, RATING.NAME AS RATING
                FROM FILMS
                JOIN GENRES ON FILMS.GENRE = GENRES.GENRE_ID
                JOIN RATING ON FILMS.RATING = RATING.RATING_ID
                WHERE FILM_ID = :film_id;
                """;
        
        return Optional.empty();
    }

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlGetRatingId = """
                SELECT RATING_ID FROM RATING WHERE NAME = :filmRating
                """;
        Integer idRating = jdbc.queryForObject(sqlGetRatingId, Map.of("filmRating", film.getRating().getTitle()), Integer.class);
        String sqlGetGenreId = """
                SELECT GENRE_ID FROM GENRES WHERE NAME = :filmGenre
                """;
        Integer idGenre = jdbc.queryForObject(sqlGetGenreId, Map.of("filmGenre", film.getGenre().getTitle()), Integer.class);

        Map<String, Object> param = Map.of("name", film.getName(),
                "description", film.getDescription(),
                "releaseDate", film.getReleaseDate(),
                "duration", film.getDuration(),
                "idGenre", idGenre,
                "idRating", idRating);

        String sql = """
        INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, GENRE, RATING)\s
        VALUES (:name, :description, :releaseDate, :duration, :idGenre, :idRating);
""";
        jdbc.update(sql, new MapSqlParameterSource().addValues(param), keyHolder, new String[]{"film_id"});
        film.setId(keyHolder.getKeyAs(Integer.class));
        return film;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Collection<Film> getAll() {
        return null;
    }

    @Override
    public void addLike(int idFilm, int idUser) {

    }

    @Override
    public void removeLike(int idFilm, int idUser) {

    }

    @Override
    public Collection<Film> getTopPopular(int count) {
        return null;
    }
}
