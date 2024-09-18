package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ua.kostenko.recollector.app.entity.CategoryItemCount;

import java.util.Optional;

/**
 * Repository interface for managing {@link CategoryItemCount} entities.
 * <p>
 * This interface extends {@link JpaRepository} and {@link JpaSpecificationExecutor} to provide standard CRUD operations
 * as well as support for custom queries using JPA Specifications.
 * </p>
 *
 * <p>Custom query methods include finding category item counts by category ID and user ID.</p>
 */
@Repository
public interface CategoryItemCountRepository extends JpaRepository<CategoryItemCount, Long>, JpaSpecificationExecutor<CategoryItemCount> {

    /**
     * Finds an {@link CategoryItemCount} entity by category ID and user ID.
     * <p>
     * The method returns an {@link Optional} containing the {@link CategoryItemCount} if found, or an empty
     * {@link Optional} if no matching entity is found.
     * </p>
     *
     * @param categoryId The ID of the category.
     * @param userId     The ID of the user.
     *
     * @return An {@link Optional} containing the found {@link CategoryItemCount}, or an empty {@link Optional} if not found.
     */
    Optional<CategoryItemCount> findByCategoryIdAndUserId(Long categoryId, Long userId);
}
