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
        User user = new User("name", "email@email.ru", LocalDate.now());
        user.setName("name");

        userRepository.save(user);

        assertThat(userRepository.getById(5))
                .isPresent()
                .hasValueSatisfying(userOptional ->
                    assertThat(userOptional).hasFieldOrPropertyWithValue("name", "name")
                ).hasValueSatisfying(userOptional ->
                    assertThat(userOptional).hasFieldOrPropertyWithValue("email", "email@email.ru")
                );
    }

    @Test
    public void get() {
        Optional<User> userOptional =  userRepository.getById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("name", "user1"))
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("email", "@email1.ru"));
    }

    @Test
    public void update() {
        User oldUserOptional = userRepository.getById(1).orElseThrow();
        oldUserOptional.setName("newName");

        Optional<User> newUserOptional = userRepository.update(oldUserOptional);
        assertThat(newUserOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("name", "newName"))
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("email", "@email1.ru"));
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
