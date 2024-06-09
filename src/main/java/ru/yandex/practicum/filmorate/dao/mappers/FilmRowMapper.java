package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getObject("NAME", String.class),
                rs.getObject("DESCRIPTION", String.class),
                rs.getObject("RELEASE_DATE", LocalDate.class),
                rs.getObject("DURATION", Integer.class));

        film.setId(rs.getObject("FILM_ID", Integer.class));
        film.setMpa(new Mpa(rs.getInt("RATING_ID"), rs.getString("RATING_NAME")));
        return film;
    }
}
