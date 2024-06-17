package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmExtractor implements ResultSetExtractor<Film> {
    @Override
    public Film extractData(ResultSet rs) throws SQLException, DataAccessException {
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
        return film;
    }
}
