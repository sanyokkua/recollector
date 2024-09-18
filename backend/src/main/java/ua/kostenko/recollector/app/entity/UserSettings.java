package ua.kostenko.recollector.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing the user settings for category and item configurations.
 * <p>
 * This entity is mapped to the 'user_settings' table in the 'recollector' schema.
 * It holds information such as color settings, page sizes, and timestamps for the user settings.
 * </p>
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
     * <p>
     * Maps to the "setting_id" column in the "user_settings" table.
     * This field is the primary key and is generated automatically.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    /**
     * User associated with the settings.
     * <p>
     * This field establishes a one-to-one relationship with the {@link User} entity.
     * The relationship is mandatory, and the settings are managed by the "user_id" foreign key.
     * </p>
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Category settings

    /**
     * Background color for categories.
     * <p>
     * Maps to the "category_background_color" column. This field is mandatory and stores a hex color code.
     * </p>
     */
    @Column(name = "category_background_color", length = 7, nullable = false)
    private String categoryBackgroundColor;

    /**
     * Item color for categories.
     * <p>
     * Maps to the "category_item_color" column. This field is mandatory and stores a hex color code.
     * </p>
     */
    @Column(name = "category_item_color", length = 7, nullable = false)
    private String categoryItemColor;

    /**
     * Floating action button (FAB) color for categories.
     * <p>
     * Maps to the "category_fab_color" column. This field is mandatory and stores a hex color code.
     * </p>
     */
    @Column(name = "category_fab_color", length = 7, nullable = false)
    private String categoryFabColor;

    /**
     * Page size for category listings.
     * <p>
     * Maps to the "category_page_size" column. This field is mandatory and specifies the number of items displayed per page.
     * </p>
     */
    @Column(name = "category_page_size", nullable = false)
    private Integer categoryPageSize;

    // Item settings

    /**
     * Background color for items.
     * <p>
     * Maps to the "item_background_color" column. This field is mandatory and stores a hex color code.
     * </p>
     */
    @Column(name = "item_background_color", length = 7, nullable = false)
    private String itemBackgroundColor;

    /**
     * Item color for items.
     * <p>
     * Maps to the "item_item_color" column. This field is mandatory and stores a hex color code.
     * </p>
     */
    @Column(name = "item_item_color", length = 7, nullable = false)
    private String itemItemColor;

    /**
     * Floating action button (FAB) color for items.
     * <p>
     * Maps to the "item_fab_color" column. This field is mandatory and stores a hex color code.
     * </p>
     */
    @Column(name = "item_fab_color", length = 7, nullable = false)
    private String itemFabColor;

    /**
     * Page size for item listings.
     * <p>
     * Maps to the "item_page_size" column. This field is mandatory and specifies the number of items displayed per page.
     * </p>
     */
    @Column(name = "item_page_size", nullable = false)
    private Integer itemPageSize;

    /**
     * Timestamp for when the setting was created.
     * <p>
     * Maps to the "created_at" column. This field is automatically set when the record is created and cannot be updated.
     * </p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp for when the setting was last updated.
     * <p>
     * Maps to the "updated_at" column. This field is automatically updated whenever the record is modified.
     * </p>
     */
    @Column(name = "updated_at", nullable = false)
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
