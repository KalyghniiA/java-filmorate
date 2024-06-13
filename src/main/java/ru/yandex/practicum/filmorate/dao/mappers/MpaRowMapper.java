package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaRowMapper implements RowMapper<Mpa> {

    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return new Mpa(rs.getInt("RATING_ID"), rs.getString("NAME"));
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("Данный жанр не находится в базе");
        }
    }
}
