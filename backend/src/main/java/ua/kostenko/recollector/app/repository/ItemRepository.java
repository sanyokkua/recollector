package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kostenko.recollector.app.entity.Item;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Item} entity.
 * Provides methods for database operations related to items.
 * Extends {@link JpaRepository} for basic CRUD operations and {@link JpaSpecificationExecutor} for complex queries.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    /**
     * Finds all items by the category ID they belong to.
     *
     * @param categoryId the ID of the category.
     *
     * @return a list of {@link Item} objects belonging to the specified category.
     */
    List<Item> findAllByCategory_CategoryId(Long categoryId);

    /**
     * Finds an item by its ID and the category ID it belongs to.
     *
     * @param itemId     the ID of the item.
     * @param categoryId the ID of the category.
     *
     * @return an {@link Optional} containing the found item, or empty if not found.
     */
    Optional<Item> findByItemIdAndCategory_CategoryId(Long itemId, Long categoryId);

    /**
     * Checks if an item with the given name exists in a specific category.
     *
     * @param itemName   the name of the item.
     * @param categoryId the ID of the category.
     *
     * @return {@code true} if an item with the given name exists in the category, {@code false} otherwise.
     */
    boolean existsByItemNameAndCategory_CategoryId(String itemName, Long categoryId);

    /**
     * Counts the number of items in a specific category with a given status.
     *
     * @param categoryId the ID of the category.
     * @param status     the status of the items.
     *
     * @return the count of items with the given status in the specified category.
     */
    @Query("SELECT COUNT(i) FROM Item i WHERE i.category.categoryId = :categoryId AND i.itemStatus = :status")
    Long countItemsByCategoryAndStatus(@Param("categoryId") Long categoryId, @Param("status") String status);

    /**
     * Counts the total number of items associated with a specific user.
     *
     * @param userId the ID of the user.
     *
     * @return the total count of items associated with the user.
     */
    @Query("SELECT COUNT(i) FROM Item i WHERE i.category.user.userId = :userId")
    Long countAllItemsByUserId(@Param("userId") Long userId);

    /**
     * Counts the number of items associated with a specific user and status.
     *
     * @param userId the ID of the user.
     * @param status the status of the items.
     *
     * @return the count of items with the given status associated with the user.
     */
    @Query("SELECT COUNT(i) FROM Item i WHERE i.category.user.userId = :userId AND i.itemStatus = :status")
    Long countAllItemsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

}
