package ua.kostenko.recollector.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing a category of items.
 * <p>
 * This entity is mapped to the 'categories' table in the 'recollector' schema.
 * It holds information about the category's name, creation and update timestamps, and related items.
 * </p>
 *
 * <p>Includes JPA annotations for persistence management and lifecycle callbacks for timestamps.</p>
 */
@Entity
@Table(name = "categories", schema = "recollector")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /**
     * Unique identifier for the category.
     * <p>
     * Maps to the "category_id" column in the "categories" table. This field is the primary key and is generated automatically.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    /**
     * The user associated with the category.
     * <p>
     * Maps to the "user_id" column. This field establishes a many-to-one relationship with the {@link User} entity.
     * The relationship is mandatory, indicating that every category must be associated with a user.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The name of the category.
     * <p>
     * Maps to the "category_name" column. This field is mandatory and holds the name of the category.
     * </p>
     */
    @Column(name = "category_name", nullable = false)
    private String categoryName;

    /**
     * Timestamp for when the category was created.
     * <p>
     * Maps to the "created_at" column. This field is automatically set when the record is created and cannot be updated.
     * </p>
     */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp for when the category was last updated.
     * <p>
     * Maps to the "updated_at" column. This field is automatically updated whenever the record is modified.
     * </p>
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * List of items associated with this category.
     * <p>
     * Maps to the "items" collection. This field establishes a one-to-many relationship with the {@link Item} entity.
     * The relationship is managed by the "category" field in the {@link Item} entity.
     * </p>
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    /**
     * Sets creation and update timestamps before persisting the entity.
     * <p>
     * Called before the entity is persisted to ensure "createdAt" and "updatedAt" are set.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the timestamp before updating the entity.
     * <p>
     * Called before the entity is updated to ensure "updatedAt" is set.
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
