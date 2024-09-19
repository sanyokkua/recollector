package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kostenko.recollector.app.entity.InvalidatedToken;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for managing {@link InvalidatedToken} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide standard CRUD operations and custom query methods
 * for {@link InvalidatedToken} entities.
 * </p>
 *
 * <p>Custom query methods include searching tokens by user ID and token, and deleting expired tokens.</p>
 */
@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Long> {

    /**
     * Finds an {@link InvalidatedToken} entity by user ID and token.
     * <p>
     * The method returns an {@link Optional} containing the {@link InvalidatedToken} if found, or an empty
     * {@link Optional} if no matching entity is found.
     * </p>
     *
     * @param userId The ID of the user associated with the token.
     * @param token  The token string to search for.
     *
     * @return An {@link Optional} containing the found {@link InvalidatedToken}, or an empty {@link Optional} if not found.
     */
    Optional<InvalidatedToken> findByUser_UserIdAndToken(Long userId, String token);

    /**
     * Deletes {@link InvalidatedToken} entities that have expired.
     * <p>
     * This method removes tokens from the database where the expiry timestamp is before the specified time.
     * </p>
     *
     * @param now The current timestamp to compare against the token expiry times.
     */
    void deleteByExpiresAtBefore(LocalDateTime now);
}
