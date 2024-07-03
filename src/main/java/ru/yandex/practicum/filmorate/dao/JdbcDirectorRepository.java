package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.DirectorExtractor;
import ru.yandex.practicum.filmorate.dao.extractors.DirectorsExtractor;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Director> getById(int directorId) {
        String sql = "SELECT NAME, DIRECTOR_ID FROM DIRECTORS WHERE DIRECTOR_ID = :director_id;";
        Map<String, Object> param = Map.of("director_id", directorId);
        return Optional.ofNullable(jdbc.query(sql, param, new DirectorExtractor()));
    }

    @Override
    public List<Director> getById(List<Integer> directorsId) {
        String sql = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS WHERE DIRECTOR_ID IN (:directors_id)";
        Map<String, Object> param = Map.of("directors_id", directorsId);
        return jdbc.query(sql, param,  new DirectorsExtractor());
    }

    @Override
    public List<Director> get() {
        String sql = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS;";
        return jdbc.query(sql, new DirectorsExtractor());
    }

    @Override
    public Director create(Director director) {
        KeyHolder kh = new GeneratedKeyHolder();
        String sql = "INSERT INTO DIRECTORS(NAME) VALUES ( :name );";
        Map<String, Object> param = Map.of("name", director.getName());
        jdbc.update(sql, new MapSqlParameterSource().addValues(param), kh, new String[]{"director_id"});
        director.setId(kh.getKeyAs(Integer.class));
        return director;
    }

    @Override
    public void update(Director director) {
        String sql = "UPDATE DIRECTORS SET NAME = :name WHERE DIRECTOR_ID = :director_id;";
        Map<String, Object> param = Map.of("name", director.getName(), "director_id", director.getId());
        jdbc.update(sql, param);
    }

    @Override
    public void delete(int directorId) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = :director_id;";
        Map<String, Object> param = Map.of("director_id", directorId);
        jdbc.update(sql, param);
    }

}
