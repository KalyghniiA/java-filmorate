package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserExtractor implements ResultSetExtractor<User> {
    @Override
    public User extractData(ResultSet rs) throws SQLException, DataAccessException {
        User user = null;
        while (rs.next()) {
            user = new User(
                    rs.getString("LOGIN"),
                    rs.getString("EMAIL"),
                    rs.getDate("BIRTHDAY").toLocalDate()
            );
            user.setName(rs.getString("NAME"));
            user.setId(rs.getInt("USER_ID"));
        }
        return user;
    }
}
