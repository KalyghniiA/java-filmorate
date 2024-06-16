package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User(
                    rs.getString("LOGIN"),
                    rs.getString("EMAIL"),
                    rs.getDate("BIRTHDAY").toLocalDate()
            );
            user.setName(rs.getString("NAME"));
            user.setId(rs.getInt("USER_ID"));
            users.add(user);
        }

        return users;
    }
}
