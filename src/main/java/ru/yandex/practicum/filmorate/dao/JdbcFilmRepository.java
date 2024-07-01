package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
                       RATINGS.NAME AS RATING_NAME,
                       GENRES.GENRE_ID AS GENRE_ID,
                       GENRES.NAME AS GENRE_NAME,
                       DIRECTORS.DIRECTOR_ID AS DIRECTOR_ID,
                       DIRECTORS.NAME AS DIRECTOR_NAME
                FROM FILMS
                        JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                        LEFT JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID
                        LEFT JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID
                        LEFT JOIN FILM_DIRECTORS ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID
                        LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
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


        saveGenresForFilm(film.getId(), film.getGenres());
        saveDirectorsForFilm(film.getId(), film.getDirectors());
        jdbc.update(sql, param);
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
    public List<Film> getFilmsById(List<Integer> filmsId) {
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
                WHERE FILMS.FILM_ID IN (:films_id)
                GROUP BY ID;
                """;

        Map<String, Object> param = Map.of("films_id", filmsId);
        return getFilms(sql, param);
    }

    @Override
    public List<Film> getTopPopularWithFilter(Integer count, Integer year, Integer genreId) {
        Map<String, Object> param = new HashMap<>();

        String concatJoin;
        String concatWhere;
        String concatLimit;
        if (year != null && genreId != null) {
            concatJoin = "JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID \n";
            concatWhere = "WHERE YEAR(FILMS.RELEASE_DATE) = :year AND FILM_GENRES.GENRE_ID = :genreId \n";
            param.put("year", year);
            param.put("genreId", genreId);
        } else if (year == null && genreId != null) {
            concatJoin = "JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID \n";
            concatWhere = "WHERE FILM_GENRES.GENRE_ID = :genreId \n";
            param.put("genreId", genreId);
        } else if (year != null) {
            concatJoin = " \n";
            concatWhere = "WHERE YEAR(FILMS.RELEASE_DATE) = :year \n";
            param.put("year", year);
        } else {
            concatJoin = " \n";
            concatWhere = " \n";
        }
        if (count != null) {
            concatLimit = """
                    LIMIT :count ;
                    """;
            param.put("count", count);
        } else {
            concatLimit = """
                    ";"
                    """;
        }
        String baseSql = """
                SELECT
                    FILMS.FILM_ID,
                    FILMS.NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    FILMS.RATING_ID,
                    RATINGS.NAME AS RATING_NAME
                FROM FILMS
                         LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                         JOIN RATINGS ON FILMS.RATING_ID = RATINGS.RATING_ID
                """;

        String bodySql = """
                GROUP BY FILMS.FILM_ID
                ORDER BY count(LIKES.FILM_ID) DESC
                """;

        String finalSql = baseSql + concatJoin + concatWhere + bodySql + concatLimit;

        return getFilms(finalSql, param);
    }

    public List<Film> getFilmsToDirector(int directorId, String by) {
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
                """;

        String orderBy = switch (by) {
            case "year" -> "ORDER BY YEAR(RELEASE_DATE);";
            case "likes" -> "ORDER BY COUNT(LIKES.FILM_ID) DESC";
            default -> throw new ValidationException("Данный вид сортировки не обрабатывается");
        };

        String concatSql = sql + orderBy;

        Map<String, Object> param = Map.of("director_id", directorId);

        return getFilms(concatSql, param);
    }

    @Override
    public List<Film> searchFilmIds(String query, String by) {
        String concatLike = "%" + query.toLowerCase() + "%";
        String concatWhere;
        if (by.equals("title")) {
            concatWhere = "WHERE LOWER(FILMS.NAME) LIKE :concatLike";
        } else if (by.equals("director")) {
            concatWhere = "WHERE LOWER(DIRECTORS.NAME) LIKE :concatLike";
        } else {
            concatWhere = "WHERE LOWER(FILMS.NAME) LIKE :concatLike OR LOWER(DIRECTORS.NAME) LIKE :concatLike";
        }
        String baseSql = """
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
                """;

        String endSql = """
                    GROUP BY FILMS.FILM_ID
                    ORDER BY count(LIKES.FILM_ID) DESC
                """;

        String finalSql = baseSql + concatWhere + endSql;

        Map<String, Object> param = Map.of("concatLike", concatLike);

        return getFilms(finalSql, param);
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
    public List<Integer> getRecommendationFilmIdByUserId(int userId) {
        String sql = """
                with user_films as (SELECT FILM_ID
                                    FROM LIKES
                                    WHERE USER_ID = :user_id)
                SELECT FILM_ID
                FROM LIKES
                WHERE USER_ID in
                      (SELECT USER_ID
                       FROM LIKES
                       WHERE USER_ID in
                             (SELECT USER_ID
                              FROM LIKES
                              WHERE FILM_ID in
                                    (SELECT FILM_ID
                                     FROM user_films))
                       GROUP BY USER_ID
                       ORDER BY count(FILM_ID) DESC
                       LIMIT 1)
                EXCEPT
                SELECT FILM_ID
                FROM user_films;
                """;
        Map<String, Object> param = Map.of("user_id", userId);
        return jdbc.query(sql, param, new RecommendationFilmIdByUserIdExtractor());
    }

    private List<Film> getFilms(String sql, Map<String, Object> param) {
        List<Film> films = jdbc.query(sql, param, new FilmsExtractor());
        if (films != null) {
            fillingFilmsWithGenres(films);
            fillingFilmsWithDirectors(films);
        }
        return films;
    }

    private void saveGenresForFilm(int filmId, Set<Genre> genres) {
        String sqlDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = :film_id;";
        String sqlInsert = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES ( :film_id, :genre_id );";

        Map<String, Object>[] batchOfInputs = new HashMap[genres.size()];
        int count = 0;

        for (Genre genre : genres) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("genre_id", genre.getId());
            batchOfInputs[count++] = map;
        }


        if (count != 0) {
            jdbc.batchUpdate(sqlDelete, batchOfInputs);
            jdbc.batchUpdate(sqlInsert, batchOfInputs);
            return;
        }
        jdbc.update(sqlDelete, Map.of("film_id", filmId));
    }

    private void saveDirectorsForFilm(int filmId, Set<Director> directors) {
        String sqlDelete = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = :film_id";
        String sqlInsert = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ( :film_id, :director_id );";

        Map<String, Object>[] batchOfInputs = new HashMap[directors.size()];
        int count = 0;

        for (Director director : directors) {
            Map<String, Object> param = new HashMap<>();
            param.put("film_id", filmId);
            param.put("director_id", director.getId());
            batchOfInputs[count++] = param;
        }

        if (count != 0) {
            jdbc.batchUpdate(sqlDelete, batchOfInputs);
            jdbc.batchUpdate(sqlInsert, batchOfInputs);
            return;
        }
        jdbc.update(sqlDelete, Map.of("film_id", filmId));
    }

    private void fillingFilmsWithGenres(List<Film> films) {
        String sql = """
                SELECT FILM_ID,
                       FILM_GENRES.GENRE_ID AS GENRE_ID,
                       NAME
                FROM FILM_GENRES
                    LEFT JOIN GENRES ON GENRES.GENRE_ID = FILM_GENRES.GENRE_ID
                WHERE FILM_ID IN (:films_id);
                """;
        Map<String, Object> param = Map.of("films_id", films.stream().map(Film::getId).toList());
        Map<Integer, List<Genre>> genres = jdbc.query(sql, param, new GenresForFilmsExtractor());

        films.forEach(film -> {
                if (genres != null && genres.containsKey(film.getId())) {
                    film.getGenres().addAll(genres.get(film.getId()));
                }
        });
    }

    private void fillingFilmsWithDirectors(List<Film> films) {
        String sql = """
                SELECT FILM_ID,
                       DIRECTORS.DIRECTOR_ID AS DIRECTOR_ID,
                       NAME
                FROM FILM_DIRECTORS
                LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
                WHERE FILM_ID IN (:films_id);
                """;
        Map<String, Object> param = Map.of("films_id", films.stream().map(Film::getId).toList());
        Map<Integer, List<Director>> directors = jdbc.query(sql, param, new DirectorsForFilmsExtractor());

        films.forEach(film -> {
                    if (directors != null && directors.containsKey(film.getId())) {
                        film.getDirectors().addAll(directors.get(film.getId()));
                    }
        });
    }
}
