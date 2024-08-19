package ua.kostenko.recollector.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kostenko.recollector.app.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {}
