package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ua.kostenko.recollector.app.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Category} entity.
 * Provides methods for database operations related to categories.
 * Extends {@link JpaRepository} for basic CRUD operations and {@link JpaSpecificationExecutor} for complex queries.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    /**
     * Finds a category by its ID and the user ID associated with it.
     *
     * @param categoryId the ID of the category.
     * @param userId     the ID of the user.
     *
     * @return an {@link Optional} containing the found category, or empty if not found.
     */
    Optional<Category> findByCategoryIdAndUser_UserId(Long categoryId, Long userId);

    /**
     * Retrieves all categories associated with a specific user.
     *
     * @param userId the ID of the user.
     *
     * @return a list of {@link Category} objects associated with the user.
     */
    List<Category> findAllByUser_UserId(Long userId);

    /**
     * Counts the number of categories associated with a specific user.
     *
     * @param userId the ID of the user.
     *
     * @return the number of categories associated with the user.
     */
    Long countByUser_UserId(Long userId);

    /**
     * Checks if a category with a given name exists for a specific user.
     *
     * @param categoryName the name of the category.
     * @param userId       the ID of the user.
     *
     * @return {@code true} if a category with the given name exists for the user, {@code false} otherwise.
     */
    boolean existsByCategoryNameAndUser_UserId(String categoryName, Long userId);

}
