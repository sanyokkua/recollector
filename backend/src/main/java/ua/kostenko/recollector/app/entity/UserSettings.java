package ua.kostenko.recollector.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing the user settings for category and item configurations.
 * This entity is mapped to the 'user_settings' table in the 'recollector' schema.
 * It holds information such as color settings, page sizes, and timestamps for the user settings.
 *
 * <p>Includes default values for the settings, timestamps for creation and updates, and JPA annotations
 * for persistence management.</p>
 *
 * <p>Fields include category and item settings such as background colors, item colors, floating action button (FAB) colors,
 * and page sizes.</p>
 */
@Entity
@Table(name = "user_settings", schema = "recollector")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    // Default settings for category and item configurations
    public static final String DEFAULT_CATEGORY_BACKGROUND_COLOR = "#f9fbe7";
    public static final String DEFAULT_CATEGORY_ITEM_COLOR = "#f0f4c3";
    public static final String DEFAULT_CATEGORY_FAB_COLOR = "#8bc34a";
    public static final int DEFAULT_CATEGORY_PAGE_SIZE = 10;
    public static final String DEFAULT_ITEM_BACKGROUND_COLOR = "#e0f7fa";
    public static final String DEFAULT_ITEM_ITEM_COLOR = "#b2ebf2";
    public static final String DEFAULT_ITEM_FAB_COLOR = "#03a9f4";
    public static final int DEFAULT_ITEM_PAGE_SIZE = 10;

    /**
     * Unique identifier for the user setting.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    /**
     * User associated with the settings. This relationship is mandatory and
     * managed by a one-to-one association.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Category settings

    /**
     * Background color for categories.
     */
    @Column(name = "category_background_color", length = 7, nullable = false)
    private String categoryBackgroundColor;

    /**
     * Item color for categories.
     */
    @Column(name = "category_item_color", length = 7, nullable = false)
    private String categoryItemColor;

    /**
     * Floating action button (FAB) color for categories.
     */
    @Column(name = "category_fab_color", length = 7, nullable = false)
    private String categoryFabColor;

    /**
     * Page size for category listings.
     */
    @Column(name = "category_page_size", nullable = false)
    private Integer categoryPageSize;

    // Item settings

    /**
     * Background color for items.
     */
    @Column(name = "item_background_color", length = 7, nullable = false)
    private String itemBackgroundColor;

    /**
     * Item color for items.
     */
    @Column(name = "item_item_color", length = 7, nullable = false)
    private String itemItemColor;

    /**
     * Floating action button (FAB) color for items.
     */
    @Column(name = "item_fab_color", length = 7, nullable = false)
    private String itemFabColor;

    /**
     * Page size for item listings.
     */
    @Column(name = "item_page_size", nullable = false)
    private Integer itemPageSize;

    /**
     * Timestamp for when the setting was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp for when the setting was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Sets creation and update timestamps before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the timestamp before updating the entity.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
