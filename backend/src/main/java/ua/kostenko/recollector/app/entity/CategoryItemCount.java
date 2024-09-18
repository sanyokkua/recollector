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

/**
 * Entity class representing the aggregated counts of items for each category.
 * <p>
 * This entity is mapped to the 'category_item_counts' view in the 'recollector' schema.
 * The view aggregates counts of items based on their status and is not a regular table but a custom SQL view.
 * </p>
 *
 * <p>Includes JPA annotations to map the view and its columns, with no lifecycle callbacks as it is read-only.</p>
 */
@Entity
@Table(name = "category_item_counts", schema = "recollector")
// Maps the entity to a database view instead of a table.
@Subselect("SELECT * FROM \"recollector\".\"category_item_counts\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItemCount {

    /**
     * Unique identifier for the category.
     * <p>
     * Maps to the "category_id" column in the "category_item_counts" view. This field is the primary key for the view.
     * </p>
     */
    @Id
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * Identifier for the user associated with the category.
     * <p>
     * Maps to the "user_id" column. Represents the user who owns the category.
     * </p>
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Name of the category.
     * <p>
     * Maps to the "category_name" column. This field holds the name of the category.
     * </p>
     */
    @Column(name = "category_name")
    private String categoryName;

    /**
     * Timestamp for when the category was created.
     * <p>
     * Maps to the "created_at" column. Indicates when the category record was initially created.
     * </p>
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp for when the category was last updated.
     * <p>
     * Maps to the "updated_at" column. Indicates the last time the category record was updated.
     * </p>
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Count of items in the category with status 'TODO_LATER'.
     * <p>
     * Maps to the "count_todo_later" column. Represents the number of items in this category that are marked as 'TODO_LATER'.
     * </p>
     */
    @Column(name = "count_todo_later")
    private Long countTodoLater;

    /**
     * Count of items in the category with status 'IN_PROGRESS'.
     * <p>
     * Maps to the "count_in_progress" column. Represents the number of items in this category that are currently 'IN_PROGRESS'.
     * </p>
     */
    @Column(name = "count_in_progress")
    private Long countInProgress;

    /**
     * Count of items in the category with status 'FINISHED'.
     * <p>
     * Maps to the "count_finished" column. Represents the number of items in this category that are marked as 'FINISHED'.
     * </p>
     */
    @Column(name = "count_finished")
    private Long countFinished;
}
