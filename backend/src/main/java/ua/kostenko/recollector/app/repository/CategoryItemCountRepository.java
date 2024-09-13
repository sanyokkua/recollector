package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ua.kostenko.recollector.app.entity.CategoryItemCount;

import java.util.Optional;

@Repository
public interface CategoryItemCountRepository extends JpaRepository<CategoryItemCount, Long>, JpaSpecificationExecutor<CategoryItemCount> {

    Optional<CategoryItemCount> findByCategoryIdAndUserId(Long categoryId, Long userId);
}
