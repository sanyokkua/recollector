package ua.kostenko.recollector.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing an item within a category.
 * <p>
 * This entity is mapped to the 'items' table in the 'recollector' schema.
 * It holds information about the item’s name, status, notes, and timestamps.
 * </p>
 *
 * <p>Includes JPA annotations for persistence management and lifecycle callbacks for timestamps.</p>
 */
@Entity
@Table(name = "items", schema = "recollector")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    /**
     * Unique identifier for the item.
     * <p>
     * Maps to the "item_id" column in the "items" table. This field is the primary key and is generated automatically.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    /**
     * The category to which this item belongs.
     * <p>
     * Maps to the "category_id" column. This field establishes a many-to-one relationship with the {@link Category} entity.
     * The relationship is mandatory, indicating that every item must be associated with a category.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * The name of the item.
     * <p>
     * Maps to the "item_name" column. This field is mandatory and holds the name of the item.
     * </p>
     */
    @Column(name = "item_name", nullable = false)
    private String itemName;

    /**
     * The status of the item.
     * <p>
     * Maps to the "item_status" column. This field is mandatory and holds a string representing the item status.
     * The status should be one of the values defined in the {@link ItemStatus} enum.
     * </p>
     */
    @Column(name = "item_status", nullable = false, length = 50)
    private String itemStatus;

    /**
     * Additional notes related to the item.
     * <p>
     * Maps to the "item_notes" column. This field is optional and can be used to store extra information about the item.
     * </p>
     */
    @Column(name = "item_notes")
    private String itemNotes;

    /**
     * Timestamp for when the item was created.
     * <p>
     * Maps to the "created_at" column. This field is automatically set when the record is created and cannot be updated.
     * </p>
     */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp for when the item was last updated.
     * <p>
     * Maps to the "updated_at" column. This field is automatically updated whenever the record is modified.
     * </p>
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

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