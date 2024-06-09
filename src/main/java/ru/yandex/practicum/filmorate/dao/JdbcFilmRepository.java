package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;

import ru.yandex.practicum.filmorate.dao.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.ParameterRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Parameter;


import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Film> getById(int id) {
        String sql = """
                SELECT FILM_ID,
                       FILMS.NAME AS NAME,
                       DESCRIPTION,
                       RELEASE_DATE,
                       DURATION,
                       RATING AS RATING_ID,
                       RATING.NAME AS RATING_NAME
                FROM FILMS
                JOIN RATING ON FILMS.RATING = RATING.RATING_ID
                WHERE FILM_ID = :film_id;
                """;
        Film film = jdbc.queryForObject(sql, Map.of("film_id", id), new FilmRowMapper());
        film.getGenres().addAll(getGenresForFilm(film.getId()));
        loadLikePerson(film);//уточнение
        return Optional.of(film);

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
        film.getGenres().forEach(genre -> {
                if (!checkDuplicateGenre(film.getId(), genre.getId())) {
                    String sqlGenre = "INSERT INTO GENRES(FILM_ID, GENRE_ID) VALUES ( :film_id, :genre_id );";
                    Map<String, Object> paramGenre = Map.of("film_id", film.getId(), "genre_id", genre.getId());
                    jdbc.update(sqlGenre, paramGenre);
                }
            }
        );


        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null) {
            return save(film);
        }


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
        film.getGenres().forEach(genre -> {
            String sqlGenre = "MERGE INTO GENRES(film_id, genre_id) VALUES ( :film_id, :genre_id );";
            Map<String, Object> paramGenre = Map.of("film_id", film.getId(), "genre_id", genre.getId());
            jdbc.update(sqlGenre, paramGenre);
        });

        return film;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM FILMS WHERE FILM_ID = :film_id;";
        jdbc.update(sql, Map.of("film_id", id));
    }

    @Override
    public Collection<Film> getAll() {
         String sql = """
                 SELECT FILM_ID,
                        NAME,
                        DESCRIPTION,
                        RELEASE_DATE,
                        DURATION,
                        RATING
                 FROM FILMS
                 """;
        return getFilms(sql, Map.of());
    }

    @Override
    public void addLike(int filmId, int userId) {
        Map<String, Object> param = Map.of("id_film", filmId, "id_user", userId);
        String sql = "INSERT INTO LIKES(film_id, user_id) VALUES ( :id_film, :id_user );";
        jdbc.update(sql, param);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Map<String, Object> param = Map.of("id_film", filmId, "id_user", userId);
        String sql = "DELETE FROM LIKES WHERE FILM_ID = :id_film AND USER_ID = :id_user;";
        jdbc.update(sql, param);
    }

    @Override
    public Collection<Film> getTopPopular(int count) {
        String sql = """
                SELECT
                    FILMS.FILM_ID AS ID,
                    FILMS.NAME AS NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    RATING,
                    COUNT(LIKES.FILM_ID) AS LIKE_COUNT
                FROM FILMS
                         LEFT OUTER JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID
                GROUP BY ID
                ORDER BY LIKE_COUNT DESC
                LIMIT :count;
                """;
        Map<String, Object> param = Map.of("count", count);

        return getFilms(sql, param);
    }

    @Override
    public List<Parameter> getGenres() {
        String sql = "SELECT GENRE_ID AS ID, NAME FROM GENRES_TITLE;";
        List<Parameter> genres = new ArrayList<>();
        SqlRowSet rs = jdbc.queryForRowSet(sql, Map.of());
        while (rs.next()) {
           genres.add(new Parameter(rs.getInt("ID"), rs.getString("NAME")));
        }

        return genres;
    }

    @Override
    public Parameter getGenreById(int genreId) {
        String sql = "SELECT GENRE_ID, NAME FROM GENRES_TITLE WHERE GENRE_ID = :genre_id";
        Map<String, Object> param = Map.of("genre_id", genreId);
        return jdbc.queryForObject(sql, param, new ParameterRowMapper());
    }

    @Override
    public Collection<Film> getFilmsByGenre(int genreId) {
        String sql = """
                SELECT FILM_ID,
                       NAME,
                       DESCRIPTION,
                       RELEASE_DATE,
                       DURATION,
                       RATING
                FROM FILMS
                WHERE FILM_ID IN (SELECT FILM_ID FROM GENRES WHERE GENRE_ID = :genre_id);
                """;
        Map<String, Object> param = Map.of("genre_id", genreId);

        return getFilms(sql, param);
    }

    @Override
    public List<Parameter> getRatings() {
        String sql = "SELECT RATING_ID AS ID, NAME FROM RATING;";
        List<Parameter> ratings = new ArrayList<>();
        SqlRowSet rs = jdbc.queryForRowSet(sql, Map.of());
        while (rs.next()) {
            ratings.add(new Parameter(rs.getInt("ID"), rs.getString("NAME")));
        }
        return ratings;
    }

    @Override
    public Parameter getRatingById(int ratingId) {
        String sql = "SELECT RATING_ID, NAME FROM RATING WHERE RATING_ID = :rating_id";
        Map<String, Object> param = Map.of("rating_id", ratingId);
        return jdbc.queryForObject(sql, param, new ParameterRowMapper());
    }

    @Override
    public Collection<Film> getFilmsByRating(int ratingId) {
        String sql = """
                SELECT FILM_ID,
                       FILMS.NAME AS NAME,
                       DESCRIPTION,
                       RELEASE_DATE,
                       DURATION,
                       RATING
                FROM FILMS
                WHERE RATING = :rating_id;
                """;
        Map<String, Object> param = Map.of("rating_id", ratingId);

        return getFilms(sql, param);
    }

    @Override
    public boolean checkAvailabilityFilm(int filmId) {
        String sql = "SELECT EXISTS(SELECT FILM_ID FROM FILMS WHERE FILM_ID = :film_id);";
        Map<String, Object> param = Map.of("film_id", filmId);
        return jdbc.queryForObject(sql, param, Boolean.class);
    }

    @Override
    public boolean checkAvailabilityLike(int filmId, int userId) {
        Map<String, Object> param = Map.of("id_film", filmId, "id_user", userId);
        String sqlAvailabilityCheck = """
                SELECT EXISTS(
                    SELECT FILM_ID, USER_ID from LIKES where FILM_ID = :id_film AND USER_ID = :id_user
                ) AS availability_check
                """;
        return jdbc.queryForObject(sqlAvailabilityCheck, param, Boolean.class);
    }

    @Override
    public boolean checkAvailabilityGenreId(List<Genre> genres) {
        for (Genre genre: genres) {
            String sql = "SELECT EXISTS(SELECT NAME FROM GENRES_TITLE WHERE GENRE_ID = :genre_id);";
            Map<String, Object> param = Map.of("genre_id", genre.getId());
            if (!jdbc.queryForObject(sql, param, Boolean.class)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkAvailabilityGenreId(int genreId) {
        String sql = "SELECT EXISTS(SELECT NAME FROM GENRES_TITLE WHERE GENRE_ID = :genre_id);";
        Map<String, Object> param = Map.of("genre_id", genreId);
        return jdbc.queryForObject(sql, param, Boolean.class);
    }

    @Override
    public boolean checkAvailabilityRatingId(Mpa rating) {
        String sql = "SELECT EXISTS(SELECT NAME FROM RATING WHERE RATING_ID = :rating_id);";
        Map<String, Object> param = Map.of("rating_id", rating.getId());
        return jdbc.queryForObject(sql, param, Boolean.class);
    }

    @Override
    public boolean checkAvailabilityRatingId(int ratingId) {
        String sql = "SELECT EXISTS(SELECT NAME FROM RATING WHERE RATING_ID = :rating_id);";
        Map<String, Object> param = Map.of("rating_id", ratingId);
        return jdbc.queryForObject(sql, param, Boolean.class);
    }

    private Film loadLikePerson(Film film) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = :id_film ;";
        Map<String, Object> param = Map.of("id_film", film.getId());

        SqlRowSet rowSet = jdbc.queryForRowSet(sql, param);
        while (rowSet.next()) {
            film.getLikes().add(rowSet.getInt("USER_ID"));
        }
        return film;
    }

    private Integer getRatingId(String ratingData) {
        String sqlGetRatingId = """
                SELECT RATING_ID FROM RATING WHERE NAME = :filmRating
                """;
        return jdbc.queryForObject(sqlGetRatingId, Map.of("filmRating", ratingData), Integer.class);
    }

    private Integer getGenreId(String  genreData) {
        String sqlGetGenreId = """
                SELECT GENRE_ID FROM GENRES WHERE NAME = :filmGenre
                """;
        return jdbc.queryForObject(sqlGetGenreId, Map.of("filmGenre", genreData), Integer.class);
    }

    private List<Genre> getGenresForFilm(int filmId) {
        String sql = """
            SELECT GENRES.GENRE_ID AS ID, GENRES_TITLE.NAME AS NAME
            FROM GENRES
            JOIN GENRES_TITLE ON GENRES.GENRE_ID = GENRES_TITLE.GENRE_ID
            WHERE FILM_ID = :film_id;
            """;
        Map<String, Object> param = Map.of("film_id", filmId);
        List<Genre> genres = new ArrayList<>();

        SqlRowSet rs = jdbc.queryForRowSet(sql, param);
        while (rs.next()) {
            genres.add(new Genre(rs.getInt("ID"), rs.getString("NAME")));
        }

        return genres;
    }

    private Mpa getRatingForFilm(int ratingId) {
        String sql = "SELECT NAME FROM RATING WHERE RATING_ID = :rating_id";
        Map<String, Object> param = Map.of("rating_id", ratingId);
        return jdbc.queryForObject(sql, param, new MpaRowMapper());
    }

    private Collection<Film> getFilms(String sql, Map<String, Object> param) {
        List<Film> films = new ArrayList<>();

        SqlRowSet rs = jdbc.queryForRowSet(sql, param);
        //Уточнение, как использовать тут маппер? требует RowSet
        while (rs.next()) {
            Film film = new Film(
                    rs.getString("NAME"),
                    rs.getString("DESCRIPTION"),
                    rs.getDate("RELEASE_DATE").toLocalDate(),
                    rs.getInt("DURATION")
            );
            film.setId(rs.getInt("FILM_ID"));
            film.setGenres(getGenresForFilm(film.getId()));
            film.setMpa(new Mpa(rs.getInt("RATING"), null));
            films.add(loadLikePerson(film));
        }

        return films;
    }

    private boolean checkDuplicateGenre(int filmId, int genreId) {
        String sql = "SELECT EXISTS(SELECT FILM_ID FROM GENRES WHERE FILM_ID = :film_id AND GENRE_ID = :genre_id);";
        Map<String, Object> param = Map.of("film_id", filmId, "genre_id", genreId);
        return jdbc.queryForObject(sql, param, Boolean.class);
    }
}
