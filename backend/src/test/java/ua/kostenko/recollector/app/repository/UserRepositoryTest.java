package ua.kostenko.recollector.app.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.kostenko.recollector.app.TestApplicationContextInitializer;
import ua.kostenko.recollector.app.entity.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {TestApplicationContextInitializer.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void givenValidUser_whenSave_thenUserIsSavedSuccessfully() {
        // Arrange
        User user = User.builder().email("testuser@example.com").passwordHash("password123").build();

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    void givenExistingEmail_whenFindByEmail_thenUserIsFound() {
        // Arrange
        User user = User.builder().email("finduser@example.com").passwordHash("password123").build();
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("finduser@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("finduser@example.com");
    }

    @Test
    void givenExistingEmail_whenExistsByEmail_thenReturnsTrue() {
        // Arrange
        User user = User.builder().email("existsuser@example.com").passwordHash("password123").build();
        userRepository.save(user);

        // Act
        boolean exists = userRepository.existsByEmail("existsuser@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void givenSavedUser_whenDeleteUser_thenUserIsDeleted() {
        // Arrange
        User user = User.builder().email("deleteuser@example.com").passwordHash("password123").build();
        User savedUser = userRepository.save(user);

        // Act
        userRepository.deleteById(savedUser.getUserId());

        // Assert
        Optional<User> deletedUser = userRepository.findById(savedUser.getUserId());
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    void givenUpdatedUser_whenSave_thenUserIsUpdated() {
        // Arrange
        User user = User.builder().email("updateuser@example.com").passwordHash("password123").build();
        User savedUser = userRepository.save(user);

        // Act
        savedUser.setEmail("updatedemail@example.com");
        userRepository.save(savedUser);
        Optional<User> updatedUser = userRepository.findById(savedUser.getUserId());

        // Assert
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getEmail()).isEqualTo("updatedemail@example.com");
    }

    @Test
    void givenDuplicateEmail_whenSave_thenThrowsDataIntegrityViolationException() {
        // Arrange
        User user1 = User.builder().email("uniqueemail@example.com").passwordHash("password123").build();
        userRepository.save(user1);

        User user2 = User.builder().email("uniqueemail@example.com").passwordHash("differentpassword").build();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
        });
    }

    @Test
    void givenUserWithNullEmail_whenSave_thenThrowsConstraintViolationException() {
        // Arrange
        User user = User.builder().email(null)  // invalid email
                        .passwordHash("password123").build();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    void givenUserWithNullPasswordHash_whenSave_thenThrowsConstraintViolationException() {
        // Arrange
        User user = User.builder().email("nullpasswordhash@example.com").passwordHash(null)  // invalid passwordHash
                        .build();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    void givenNonExistingUser_whenUpdate_thenNoChangeIsMade() {
        // Arrange
        Long nonExistingUserId = 999L;
        User user = User.builder()
                        .userId(nonExistingUserId)
                        .email("nonexistent@example.com")
                        .passwordHash("password123")
                        .build();

        // Act
        userRepository.save(user); // This should succeed but will not change anything

        // Assert
        Optional<User> updatedUser = userRepository.findById(nonExistingUserId);
        assertThat(updatedUser).isNotPresent();
    }

    @Test
    void givenNonExistingUser_whenDelete_thenNothingHappens() {
        // Arrange
        Long nonExistingUserId = 999L;

        // Act
        userRepository.deleteById(nonExistingUserId);

        // Assert
        Optional<User> deletedUser = userRepository.findById(nonExistingUserId);
        assertThat(deletedUser).isNotPresent();
    }
}