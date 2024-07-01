package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;
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
            do {
                int genreId = rs.getInt("GENRE_ID");
                int directorId = rs.getInt("DIRECTOR_ID");
                if (genreId != 0) {
                    film.getGenres().add(new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME")));
                }

                if (directorId != 0) {
                    Director director = new Director();
                    director.setId(rs.getInt("DIRECTOR_ID"));
                    director.setName(rs.getString("DIRECTOR_NAME"));
                    film.getDirectors().add(director);
                }

            } while (rs.next());
        }
        return film;
    }
}
