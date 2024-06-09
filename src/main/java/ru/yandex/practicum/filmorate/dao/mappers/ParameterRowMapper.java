package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Parameter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParameterRowMapper implements RowMapper<Parameter> {
    @Override
    public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Parameter(rs.getInt(1), rs.getString(2));
    }
}
