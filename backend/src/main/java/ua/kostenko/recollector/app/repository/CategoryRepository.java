package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ua.kostenko.recollector.app.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByCategoryIdAndUser_UserId(Long categoryId, Long userId);
    List<Category> findAllByUser_UserId(Long userId);
    Long countByUser_UserId(Long userId);
    boolean existsByCategoryNameAndUser_UserId(String categoryName, Long userId);
}
