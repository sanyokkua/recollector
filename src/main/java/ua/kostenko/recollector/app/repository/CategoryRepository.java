package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kostenko.recollector.app.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
