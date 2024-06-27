package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.FilmExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.FilmsExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.LikedFilmsForUserIdExtractor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

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
                       FILMS.RATING_ID,
                       RATINGS.NAME AS RATING_NAME
                FROM FILMS
                JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                WHERE FILMS.FILM_ID = :film_id;
                """;
        Map<String, Object> param = Map.of("film_id", id);
        Film film = jdbc.query(sql, param, new FilmExtractor());

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
                INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
                VALUES (:name, :description, :releaseDate, :duration, :idRating);
                """;


        jdbc.update(sql, new MapSqlParameterSource().addValues(paramFilm), keyHolder, new String[]{"film_id"});
        film.setId(keyHolder.getKeyAs(Integer.class));
        saveGenresForFilm(film.getId(), film.getGenres());
        saveDirectorsForFilm(film.getId(), film.getDirectors());
        return film;
    }

    @Override
    public void update(Film film) {
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
                        RATING_ID = :rating
                WHERE FILM_ID = :film_id;
                """;

        jdbc.update(sql, param);
        saveGenresForFilm(film.getId(), film.getGenres());
        saveDirectorsForFilm(film.getId(), film.getDirectors());
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
                       FILMS.RATING_ID,
                       RATINGS.NAME AS RATING_NAME
                FROM FILMS
                JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                GROUP BY ID;
                """;
        return getFilms(sql, Map.of());
    }

    @Override
    public List<Film> getTopPopular(Integer count) {
        String sql = """
                SELECT
                    FILMS.FILM_ID AS ID,
                    FILMS.NAME AS FILM_NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME,
                    COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT OUTER JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                GROUP BY ID
                ORDER BY LIKE_COUNT DESC
                LIMIT :count;
                """;
        Map<String, Object> param = Map.of("count", count);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getFilmsToDirectorSortByYear(int directorId) {
        String sql = """
                SELECT
                    FILMS.FILM_ID AS ID,
                    FILMS.NAME AS FILM_NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME,
                    COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT OUTER JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE FILMS.FILM_ID IN (
                    SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID = :director_id
                    )
                GROUP BY ID
                ORDER BY YEAR(RELEASE_DATE);
                """;
        Map<String, Object> param = Map.of("director_id", directorId);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getFilmsToDirectorSortByLikes(int directorId) {
        String sql = """
                SELECT
                    FILMS.FILM_ID AS ID,
                    FILMS.NAME AS FILM_NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME,
                    COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT OUTER JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE FILMS.FILM_ID IN (
                    SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID = :director_id
                    )
                GROUP BY ID
                ORDER BY LIKE_COUNT DESC;
                """;
        Map<String, Object> param = Map.of("director_id", directorId);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getPopularFilmsByYear(Integer count, Integer year) {
        String sql = """
                SELECT
                    FILMS.FILM_ID AS ID,
                    FILMS.NAME AS FILM_NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME,
                    COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT OUTER JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE YEAR(FILMS.RELEASE_DATE) = :year
                GROUP BY ID
                ORDER BY LIKE_COUNT DESC
                """;

        Map<String, Object> param = Map.of("year", year);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(Integer count, Integer genreId) {
        String sql = """
                SELECT
                    FILMS.FILM_ID AS ID,
                    FILMS.NAME AS FILM_NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME,
                    COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID
                         LEFT JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT OUTER JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE GENRES.GENRE_ID = :genreId
                GROUP BY ID, FILM_GENRES.GENRE_ID
                ORDER BY LIKE_COUNT DESC
                """;

        Map<String, Object> param = Map.of("genreId", genreId);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getPopularFilmsByYearAndGenre(Integer count, Integer year, Integer genreId) {
        String sql = """
                SELECT
                    FILMS.FILM_ID AS ID,
                    FILMS.NAME AS FILM_NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME,
                    COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID
                         LEFT JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT OUTER JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE YEAR(FILMS.RELEASE_DATE) = :year AND GENRES.GENRE_ID = :genreId
                GROUP BY ID, FILM_GENRES.GENRE_ID
                ORDER BY LIKE_COUNT DESC
                """;

        Map<String, Object> param = Map.of("year", year, "genreId", genreId);

        return getFilms(sql, param);
    }

//    @Override
//    public List<Film> getPopularFilmsWithDirectors() {
//        String sql = """
//                SELECT
//                    FILMS.FILM_ID,
//                    FILMS.NAME,
//                    DESCRIPTION,
//                    RELEASE_DATE,
//                    DURATION,
//                    FILMS.RATING_ID,
//                    RATINGS.NAME AS RATING_NAME
//                FROM FILMS
//                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
//                         LEFT JOIN FILM_DIRECTORS ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID
//                         LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
//                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
//                GROUP BY FILMS.FILM_ID
//                ORDER BY count(LIKES.FILM_ID) DESC;
//                """;
//
//        return getFilms(sql, Map.of());
//    }

    @Override
    public List<Film> getSearchedFiltrByTitleAndDirector(String query) {
        String sql = """
                SELECT
                    FILMS.FILM_ID,
                    FILMS.NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT JOIN FILM_DIRECTORS ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID
                         LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE LOWER(FILMS.NAME) LIKE LOWER('%" + query + "%') OR LOWER(DIRECTORS.NAME) LIKE LOWER('%" + query + "%')
                GROUP BY FILMS.FILM_ID
                ORDER BY count(LIKES.FILM_ID) DESC;
                """;

        Map<String, Object> param = Map.of("query", query);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getSearchedFiltrByTitle(String query) {
        String sql = """
                SELECT
                    FILMS.FILM_ID,
                    FILMS.NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT JOIN FILM_DIRECTORS ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID
                         LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE LOWER(FILMS.NAME) LIKE LOWER('%" + query + "%')
                GROUP BY FILMS.FILM_ID
                ORDER BY count(LIKES.FILM_ID) DESC;
                """;

        Map<String, Object> param = Map.of("query", query);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getSearchedFiltrByDirector(String query) {
        String sql = """
                SELECT
                    FILMS.FILM_ID,
                    FILMS.NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT JOIN FILM_DIRECTORS ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID
                         LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE LOWER(DIRECTORS.NAME) LIKE LOWER('%" + query + "%')
                GROUP BY FILMS.FILM_ID
                ORDER BY count(LIKES.FILM_ID) DESC;
                """;

        Map<String, Object> param = Map.of("query", query);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getSearchedFiltrByTitleAndDirector(String query) {
        String concatParam = "%" + query + "%";
        String sql = """
                SELECT
                    FILMS.FILM_ID,
                    FILMS.NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT JOIN FILM_DIRECTORS ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID
                         LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE FILMS.NAME LIKE :query OR DIRECTORS.NAME LIKE :query
                GROUP BY FILMS.FILM_ID
                ORDER BY count(LIKES.FILM_ID) DESC;
                """;

        Map<String, Object> param = Map.of("query", concatParam);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getSearchedFiltrByTitle(String query) {
        String concatParam = "%" + query + "%";
        String sql = """
                SELECT
                    FILMS.FILM_ID,
                    FILMS.NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT JOIN FILM_DIRECTORS ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID
                         LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE FILMS.NAME LIKE :query
                GROUP BY FILMS.FILM_ID
                ORDER BY count(LIKES.FILM_ID) DESC;
                """;

        Map<String, Object> param = Map.of("query", concatParam);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getSearchedFiltrByDirector(String query) {
        String concatParam = "%" + query + "%";
        String sql = """
                SELECT
                    FILMS.FILM_ID,
                    FILMS.NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT JOIN FILM_DIRECTORS ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID
                         LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE DIRECTORS.NAME LIKE :query
                GROUP BY FILMS.FILM_ID
                ORDER BY count(LIKES.FILM_ID) DESC;
                """;

        Map<String, Object> param = Map.of("query", concatParam);

        return getFilms(sql, param);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = """
                SELECT FILMS.FILM_ID AS ID,
                       FILMS.NAME AS FILM_NAME,
                       DESCRIPTION,
                       RELEASE_DATE,
                       DURATION,
                       FILMS.RATING_ID,
                       RATINGS.NAME AS RATING_NAME,
                       COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                WHERE FILMS.FILM_ID IN (SELECT DISTINCT LIKES.FILM_ID
                                        FROM LIKES
                                        WHERE USER_ID = :user_id
                                        INTERSECT
                                        SELECT DISTINCT LIKES.FILM_ID
                                        FROM LIKES
                                        WHERE  USER_ID = :friend_id)
                GROUP BY ID
                ORDER BY LIKE_COUNT DESC;
                """;

        Map<String, Object> param = Map.of("user_id", userId, "friend_id", friendId);

        return getFilms(sql, param);
    }

    @Override
    public List<Integer> getLikedFilmsByUserId(int userId) {
        String sql = """
                SELECT  LIKES.FILM_ID
                FROM LIKES
                WHERE USER_ID = :user_id
                """;
        Map<String, Object> param = Map.of("user_id", userId);
        return jdbc.query(sql, param, new LikedFilmsForUserIdExtractor());
    }

    private List<Film> getFilms(String sql, Map<String, Object> param) {
        return jdbc.query(sql, param, new FilmsExtractor());
    }

    private void saveGenresForFilm(int filmId, Set<Genre> genres) {
        String sqlDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = :film_id AND GENRE_ID = :genre_id;";
        String sqlInsert = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES ( :film_id, :genre_id );";

        Map<String, Object>[] batchOfInputs = new HashMap[genres.size()];
        int count = 0;

        for (Genre genre : genres) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("genre_id", genre.getId());
            batchOfInputs[count++] = map;
        }


        jdbc.batchUpdate(sqlDelete, batchOfInputs);
        jdbc.batchUpdate(sqlInsert, batchOfInputs);
    }

    private void saveDirectorsForFilm(int filmId, Set<Director> directors) {
        String sqlDelete = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = :film_id AND DIRECTOR_ID = :director_id;";
        String sqlInsert = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ( :film_id, :director_id );";

        Map<String, Object>[] batchOfInputs = new HashMap[directors.size()];
        int count = 0;

        for (Director director : directors) {
            Map<String, Object> param = new HashMap<>();
            param.put("film_id", filmId);
            param.put("director_id", director.getId());
            batchOfInputs[count++] = param;
        }

        jdbc.batchUpdate(sqlDelete, batchOfInputs);
        jdbc.batchUpdate(sqlInsert, batchOfInputs);
    }
}
