package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsExtractor implements ResultSetExtractor<List<Integer>> {
    @Override
    public List<Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Integer> friends = new ArrayList<>();
        while (rs.next()) {
            friends.add(rs.getInt("FRIEND_ID"));
        }
        return friends;
    }
}
