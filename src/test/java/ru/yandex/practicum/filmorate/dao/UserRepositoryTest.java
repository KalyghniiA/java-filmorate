package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@Import(JdbcUserRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {
    private final JdbcUserRepository userRepository;

    @Test
    public void save() {
        LocalDate date = LocalDate.now();
        User user = new User("name", "email@email.ru", date);
        user.setName("name");

        userRepository.save(user);

        assertThat(userRepository.getById(5))
                .isPresent()
                .hasValueSatisfying(userOptional -> {
                    assertThat(userOptional).hasFieldOrPropertyWithValue("name", "name");
                    assertThat(userOptional).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(userOptional).hasFieldOrPropertyWithValue("login", "name");
                    assertThat(userOptional).hasFieldOrPropertyWithValue("birthday", date);
                });
    }

    @Test
    public void get() {
        Optional<User> userOptional =  userRepository.getById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("name", "user1");
                    assertThat(user).hasFieldOrPropertyWithValue("email", "@email1.ru");
                    assertThat(user).hasFieldOrPropertyWithValue("login", "user1");
                    assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2020, 10, 10));
                });
    }

    @Test
    public void update() {
        User oldUserOptional = userRepository.getById(1).orElseThrow();
        oldUserOptional.setName("newName");
        userRepository.update(oldUserOptional);
        Optional<User> newUserOptional = userRepository.getById(1);
        assertThat(newUserOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("name", "newName");
                    assertThat(user).hasFieldOrPropertyWithValue("email", "@email1.ru");
                    assertThat(user).hasFieldOrPropertyWithValue("login", "user1");
                    assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2020, 10, 10));
                });
    }

    @Test
    public void getAll() {
        List<User> users = userRepository.getAll();

        assertThat(users.size()).isEqualTo(4);
    }

    @Test
    public void addFriend() {
        userRepository.addFriend(1, 2);
        List<User> friends = userRepository.getFriends(1);

        assertThat(friends.get(0))
                .hasFieldOrPropertyWithValue("name", "user2");
        assertThat(friends.size()).isEqualTo(1);
    }

    @Test
    public void removeFriend() {
        userRepository.addFriend(1, 2);
        userRepository.addFriend(1, 3);

        userRepository.deleteFriend(1, 3);

        List<User> friends = userRepository.getFriends(1);
        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.get(0))
                .hasFieldOrPropertyWithValue("name", "user2");
    }

    @Test
    public void getMutualFriends() {
        userRepository.addFriend(1, 2);
        userRepository.addFriend(3, 2);
        userRepository.addFriend(1, 4);

        List<User> mutualFriends = userRepository.getMutualFriends(1, 3);

        assertThat(mutualFriends.size()).isEqualTo(1);
        assertThat(mutualFriends.get(0)).hasFieldOrPropertyWithValue("name", "user2");
    }
}
