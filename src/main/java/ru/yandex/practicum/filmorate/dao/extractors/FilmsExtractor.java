package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class FilmsExtractor implements ResultSetExtractor<Map<Integer, Film>> {
    @Override
    public Map<Integer, Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> films = new LinkedHashMap<>();
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

        return films;
    }
}
