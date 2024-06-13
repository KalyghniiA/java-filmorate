package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Film> getById(int id) {
        String sql = """
                SELECT FILMS.FILM_ID AS ID,
                       FILMS.NAME AS FILM_NAME,
                       DESCRIPTION,
                       RELEASE_DATE,
                       DURATION,
                       RATING AS RATING_ID,
                       RATINGS.NAME AS RATING_NAME,
                       GENRES.GENRE_ID AS GENRE_ID,
                       GENRES.NAME AS GENRE_NAME
                FROM FILMS
                JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID
                LEFT JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                JOIN RATINGS ON FILMS.RATING = RATINGS.RATING_ID
                WHERE FILMS.FILM_ID = :film_id
                order by GENRE_ID
                """;
        Map<String, Object> param = Map.of("film_id", id);
        SqlRowSet rs = jdbc.queryForRowSet(sql, param);
        Film film = null;
        if (rs.next()) {
            film = new Film(
                    rs.getString("FILM_NAME"),
                    rs.getString("DESCRIPTION"),
                    rs.getDate("RELEASE_DATE").toLocalDate(),
                    rs.getInt("DURATION")
            );
            film.setId(rs.getInt("ID"));
            film.setMpa(new Mpa(rs.getInt("RATING_ID"), rs.getString("RATING_NAME")));
            int idGenre = rs.getInt("GENRE_ID");
            if (idGenre != 0) {
                film.getGenres().add(new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME")));
                while (rs.next()) {
                    film.getGenres().add(new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME")));
                }
            }
        }


        return Optional.ofNullable(film);

    }

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        Map<String, Object> paramFilm = Map.of("name", film.getName(),
                "description", film.getDescription(),
                "releaseDate", film.getReleaseDate(),
                "duration", film.getDuration(),
                "idRating", film.getMpa().getId());

        String sql = """
        INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING)
        VALUES (:name, :description, :releaseDate, :duration, :idRating);
        """;


        jdbc.update(sql, new MapSqlParameterSource().addValues(paramFilm), keyHolder, new String[]{"film_id"});
        film.setId(keyHolder.getKeyAs(Integer.class));
        saveGenresForFilm(film.getId(), film.getGenres());

        return film;
    }

    @Override
    public Film update(Film film) {
        Map<String, Object> param = Map.of(
                "film_id", film.getId(),
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "rating", film.getMpa().getId()
        );

        String sql = """
                UPDATE FILMS
                    SET NAME = :name,
                        DESCRIPTION = :description,
                        RELEASE_DATE = :release_date,
                        DURATION = :duration,
                        RATING = :rating
                WHERE FILM_ID = :film_id;
                """;

        jdbc.update(sql, param);
        saveGenresForFilm(film.getId(), film.getGenres());

        return film;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM FILMS WHERE FILM_ID = :film_id;";
        jdbc.update(sql, Map.of("film_id", id));
    }

    @Override
    public List<Film> getAll() {
         String sql = """
                 SELECT FILMS.FILM_ID AS ID,
                        FILMS.NAME AS FILM_NAME,
                        DESCRIPTION,
                        RELEASE_DATE,
                        DURATION,
                        FILMS.RATING AS RATING_ID,
                        RATINGS.NAME AS RATING_NAME,
                        GENRES.GENRE_ID AS GENRE_ID,
                        GENRES.NAME AS GENRE_NAME
                 FROM FILMS
                 JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID
                 LEFT JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                 JOIN RATINGS ON FILMS.RATING = RATINGS.RATING_ID;
                 """;
        return getFilms(sql, Map.of());
    }

    @Override
    public List<Film> getTopPopular(int count) {
        String sql = """
                SELECT
                    FILMS.FILM_ID AS ID,
                    FILMS.NAME AS FILM_NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    RATING AS RATING_ID,
                    RATINGS.NAME AS RATING_NAME,
                    GENRES.GENRE_ID AS GENRE_ID,
                    GENRES.NAME AS GENRE_NAME,
                    COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID
                         LEFT JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                         JOIN RATINGS ON FILMS.RATING = RATINGS.RATING_ID
                         LEFT OUTER JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                GROUP BY FILM_GENRES.GENRE_ID, ID
                ORDER BY LIKE_COUNT DESC
                LIMIT :count;
                """;
        Map<String, Object> param = Map.of("count", count);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getFilmsByGenre(int genreId) {
        String sql = """
                SELECT FILMS.FILM_ID AS ID,
                       FILMS.NAME AS FILM_NAME,
                       DESCRIPTION,
                       RELEASE_DATE,
                       DURATION,
                       RATING AS RATING_ID,
                       RATINGS.NAME AS RATING_NAME,
                       GENRES.GENRE_ID AS GENRE_ID,
                       GENRES.NAME AS GENRE_NAME
                FROM FILMS
                JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID
                JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                JOIN RATINGS ON FILMS.RATING = RATINGS.RATING_ID
                WHERE FILMS.FILM_ID IN (SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = :genre_id);
                """;
        Map<String, Object> param = Map.of("genre_id", genreId);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getFilmsByRating(int ratingId) {
        String sql = """
                SELECT FILMS.FILM_ID AS ID,
                       FILMS.NAME AS FILM_NAME,
                       DESCRIPTION,
                       RELEASE_DATE,
                       DURATION,
                       RATING AS RATING_ID,
                       RATINGS.NAME AS RATING_NAME,
                       GENRES.GENRE_ID AS GENRE_ID,
                       GENRES.NAME AS GENRE_NAME
                FROM FILMS
                JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID
                JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                JOIN RATINGS ON FILMS.RATING = RATINGS.RATING_ID
                WHERE RATING = :rating_id;
                """;
        Map<String, Object> param = Map.of("rating_id", ratingId);

        return getFilms(sql, param);
    }

    private List<Film> getFilms(String sql, Map<String, Object> param) {
       Map<Integer, Film> films = new LinkedHashMap<>();

       SqlRowSet rs = jdbc.queryForRowSet(sql, param);
       while (rs.next()) {
           Integer filmId = rs.getInt("FILM_ID");
           if (films.containsKey(filmId)) {
               films.get(filmId)
                       .getGenres()
                       .add(new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME")));
               continue;
           }


           Film film = new Film(
                   rs.getString("FILM_NAME"),
                   rs.getString("DESCRIPTION"),
                   rs.getDate("RELEASE_DATE").toLocalDate(),
                   rs.getInt("DURATION")
           );
           film.setId(filmId);
           film.setMpa(new Mpa(rs.getInt("RATING_ID"), rs.getString("RATING_NAME")));
           film.getGenres().add(new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME")));
           films.put(filmId, film);

       }

       return films.values().stream().toList();
    }

    private void saveGenresForFilm(int filmId, Set<Genre> genres) {
        Map<String, Object>[] batchOfInputs = new HashMap[genres.size()];
        String sqlDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = :film_id AND GENRE_ID = :genre_id;";
        String sqlInsert = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES ( :film_id, :genre_id );";
        String sqlInsertNull = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES ( :film_id, NULL );";
        int count = 0;

        if (genres.isEmpty()) {
            jdbc.update(sqlInsertNull, Map.of("film_id", filmId));
            return;
        }

        for (Genre genre: genres) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("genre_id", genre.getId());
            batchOfInputs[count++] = map;
        }

        jdbc.batchUpdate(sqlDelete, batchOfInputs);
        try {
            jdbc.batchUpdate(sqlInsert, batchOfInputs);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("Один из жанров отсутствует в базе");
        }
    }
}
