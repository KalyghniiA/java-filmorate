package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.DirectorExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.DirectorsExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.DirectorsForFilmExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.DirectorsForFilmsExtractor;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Director> getDirectorById(int directorId) {
        String sql = "SELECT NAME, DIRECTOR_ID FROM DIRECTORS WHERE DIRECTOR_ID = :director_id;";
        Map<String, Object> param = Map.of("director_id", directorId);
        return Optional.ofNullable(jdbc.query(sql, param, new DirectorExtractor()));
    }

    @Override
    public List<Director> getDirectorsById(List<Integer> directorsId) {
        String sql = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS WHERE DIRECTOR_ID IN (:directors_id)";
        Map<String, Object> param = Map.of("directors_id", directorsId);
        return jdbc.query(sql, param,  new DirectorsExtractor());
    }

    @Override
    public Set<Director> getDirectorsByFilm(int filmId) {
        String sql = """
                SELECT DIRECTORS.DIRECTOR_ID AS DIRECTOR_ID,
                       NAME
                FROM DIRECTORS
                JOIN FILM_DIRECTORS ON DIRECTORS.DIRECTOR_ID = FILM_DIRECTORS.DIRECTOR_ID
                WHERE FILM_DIRECTORS.FILM_ID = :film_id;
                """;

        Map<String, Object> param = Map.of("film_id", filmId);
        return jdbc.query(sql, param, new DirectorsForFilmExtractor());
    }

    @Override
    public Map<Integer, Set<Director>> getDirectorsByFilms(List<Integer> filmsId) {
        String sql = """
                SELECT FILM_ID,
                       DIRECTORS.DIRECTOR_ID AS DIRECTOR_ID,
                       NAME
                FROM FILM_DIRECTORS
                LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID
                WHERE FILM_ID IN (:films_id);
                """;
        Map<String, Object> param = Map.of("films_id", filmsId);
        return jdbc.query(sql, param, new DirectorsForFilmsExtractor());
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS;";
        return jdbc.query(sql, new DirectorsExtractor());
    }

    @Override
    public Director createDirector(Director director) {
        KeyHolder kh = new GeneratedKeyHolder();
        String sql = "INSERT INTO DIRECTORS(NAME) VALUES ( :name );";
        Map<String, Object> param = Map.of("name", director.getName());
        jdbc.update(sql, new MapSqlParameterSource().addValues(param), kh, new String[]{"director_id"});
        director.setId(kh.getKeyAs(Integer.class));
        return director;
    }

    @Override
    public void updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET NAME = :name WHERE DIRECTOR_ID = :director_id;";
        Map<String, Object> param = Map.of("name", director.getName(), "director_id", director.getId());
        jdbc.update(sql, param);
    }

    @Override
    public void deleteDirector(int directorId) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = :director_id;";
        Map<String, Object> param = Map.of("director_id", directorId);
        jdbc.update(sql, param);
    }

}
