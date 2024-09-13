package ua.kostenko.recollector.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Subselect;

import java.time.LocalDateTime;

@Entity
@Table(name = "category_item_counts", schema = "recollector")
// Allows mapping the entity to a view or custom query without triggering validation errors.
@Subselect("SELECT * FROM "recollector"."category_item_counts"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItemCount {

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "count_todo_later")
    private Long countTodoLater;

    @Column(name = "count_in_progress")
    private Long countInProgress;

    @Column(name = "count_finished")
    private Long countFinished;
}
