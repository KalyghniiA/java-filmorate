package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> param = Map.of(
                "login", user.getLogin(),
                "name", user.getName(),
                "email", user.getEmail(),
                "birthday", user.getBirthday()
        );
        String sql = """
                INSERT INTO USERS(LOGIN, NAME, EMAIL, BIRTHDAY)
                VALUES ( :login, :name, :email, :birthday);
                """;

        try {
            jdbc.update(sql, new MapSqlParameterSource().addValues(param), keyHolder, new String[]{"user_id"});
        } catch (DuplicateKeyException e) {
            throw new DuplicateDataException("Указанный логин или почта уже зарегистрирована в базе");
        }
        user.setId(keyHolder.getKeyAs(Integer.class));
        return user;
    }

    @Override
    public void delete(int userId) {
        String sql = "DELETE FROM USERS WHERE USER_ID = :user_id";
        Map<String, Object> param = Map.of("user_id", userId);
        jdbc.update(sql, param);
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            save(user);
        }

        Map<String, Object> param = Map.of(
                "user_id", user.getId(),
                "login", user.getLogin(),
                "name", user.getName(),
                "email", user.getEmail(),
                "birthday", user.getBirthday()
        );

        String sql = """
                UPDATE USERS
                    SET LOGIN = :login,
                        NAME = :name,
                        EMAIL = :email,
                        BIRTHDAY = :birthday
                WHERE USER_ID = :user_id;
                """;

        jdbc.update(sql, param);
        return user;
    }

    @Override
    public User getById(int userId) {
        Map<String, Object> param = Map.of("user_id", userId);
        String sql = """
                SELECT USER_ID, LOGIN, NAME, EMAIL, BIRTHDAY
                FROM USERS
                WHERE USER_ID = :user_id
                """;
        String sqlFriends = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = :user_id;";

        User user = jdbc.queryForObject(sql, param, new UserRowMapper());
        SqlRowSet rowSetFriends = jdbc.queryForRowSet(sqlFriends, param);
        while (rowSetFriends.next()) {
            user.getFriends().add(rowSetFriends.getInt("FRIEND_ID"));
        }

        return user;
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT USER_ID, LOGIN, NAME, EMAIL, BIRTHDAY FROM USERS;";
        String sqlFriends = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = :user_id;";

        List<User> users = new ArrayList<>();
        SqlRowSet rowSet = jdbc.queryForRowSet(sql, Map.of());

        while (rowSet.next()) {
            //уточнение, есть ли возможность использовать сразу строку из таблицы и передавать ей маппер
            User user = new User(
                    rowSet.getString("LOGIN"),
                    rowSet.getString("EMAIL"),
                    rowSet.getDate("BIRTHDAY").toLocalDate()
            );
            user.setName(rowSet.getString("NAME"));
            user.setId(rowSet.getInt("USER_ID"));

            Map<String, Object> param = Map.of("user_id", user.getId());
            SqlRowSet rowSetFriends = jdbc.queryForRowSet(sqlFriends, param);

            while (rowSetFriends.next()) {
                user.getFriends().add(rowSetFriends.getInt("FRIEND_ID"));
            }
            users.add(user);
        }

        return users;
    }

    @Override
    public User addFriend(int userId, int friendId) {
        String sql = "INSERT INTO FRIENDS(USER_ID, FRIEND_ID) VALUES ( :user_id, :friend_id );";
        Map<String, Object> param = Map.of(
                "user_id", userId,
                "friend_id", friendId
        );
        jdbc.update(sql, param);
        return getById(userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = :user_id AND FRIEND_ID = :friend_id;";
        Map<String, Object> param = Map.of(
                "user_id", userId,
                "friend_id", friendId
        );
        jdbc.update(sql, param);
    }

    @Override
    public Collection<User> getFriends(int userId) {
        String sql = "SELECT FRIEND_ID AS ID FROM FRIENDS WHERE USER_ID = :user_id;";
        Map<String, Object> param = Map.of("user_id", userId);
        List<User> users = new ArrayList<>();

        SqlRowSet rowSet = jdbc.queryForRowSet(sql, param);
        while (rowSet.next()) {
            users.add(getById(rowSet.getInt("ID")));
        }

        return users;
    }

    @Override
    public Collection<User> getMutualFriends(int userId, int otherId) {
        String sql = """
                SELECT FRIEND_ID AS ID
                FROM FRIENDS
                WHERE USER_ID = :user_id
                  AND FRIEND_ID = (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = :other_id);
                """;
        Map<String, Object> param = Map.of(
                "user_id", userId,
                "other_id", otherId
        );
        List<User> users = new ArrayList<>();
        SqlRowSet rowSet = jdbc.queryForRowSet(sql, param);
        while (rowSet.next()) {
            users.add(getById(rowSet.getInt("ID")));
        }

        return users;
    }

    public boolean checkIsFriends(int userId, int friendId) {
        String sql = """
                SELECT EXISTS(
                    SELECT USER_ID, FRIEND_ID FROM FRIENDS WHERE USER_ID = :user_id AND FRIEND_ID = :friend_id
                );
                """;
        Map<String, Object> param = Map.of(
                "user_id", userId,
                "friend_id", friendId
        );

        return jdbc.queryForObject(sql, param, Boolean.class);
    }
}
