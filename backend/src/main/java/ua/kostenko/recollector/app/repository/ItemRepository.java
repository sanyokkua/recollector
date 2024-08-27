package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.kostenko.recollector.app.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    List<Item> findAllByCategory_CategoryId(Long categoryId);
    Optional<Item> findByItemIdAndCategory_CategoryId(Long itemId, Long categoryId);
    boolean existsByItemNameAndCategory_CategoryId(String itemName, Long categoryId);

    @Query("SELECT COUNT(i) FROM Item i WHERE i.category.categoryId = :categoryId AND i.itemStatus = :status")
    Long countItemsByCategoryAndStatus(@Param("categoryId") Long categoryId, @Param("status") String status);

    @Query("SELECT COUNT(i) FROM Item i WHERE i.category.user.userId = :userId")
    Long countAllItemsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(i) FROM Item i WHERE i.category.user.userId = :userId AND i.itemStatus = :status")
    Long countAllItemsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

}
