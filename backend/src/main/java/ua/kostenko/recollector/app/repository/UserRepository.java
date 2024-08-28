package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kostenko.recollector.app.entity.User;

import java.util.Optional;

/**
 * Repository interface for {@link User} entity.
 * Provides methods for database operations related to users.
 * Extends {@link JpaRepository} for basic CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check.
     *
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user.
     *
     * @return an {@link Optional} containing the found user, or empty if not found.
     */
    Optional<User> findByEmail(String email);
}
