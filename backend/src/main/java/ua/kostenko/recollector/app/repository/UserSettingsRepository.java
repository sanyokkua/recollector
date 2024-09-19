package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kostenko.recollector.app.entity.UserSettings;

import java.util.Optional;

/**
 * Repository interface for the {@link UserSettings} entity.
 * Provides methods for performing CRUD operations and querying the database.
 *
 * <p>Extends {@link JpaRepository} to provide standard data access methods.</p>
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    /**
     * Finds a user's settings by the user's unique ID.
     *
     * @param userId the ID of the user.
     *
     * @return an {@link Optional} containing the found {@link UserSettings} if present.
     */
    Optional<UserSettings> findByUser_UserId(Long userId);
}
