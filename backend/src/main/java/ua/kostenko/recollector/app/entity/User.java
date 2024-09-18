package ua.kostenko.recollector.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a user in the application.
 * <p>
 * This entity maps to the "users" table in the "recollector" schema.
 * </p>
 */
@Entity
@Table(name = "users", schema = "recollector")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"categories", "settings"})
public class User {

    /**
     * Unique identifier for the user.
     * <p>
     * This field maps to the "user_id" column, which is the primary key of the "users" table.
     * It is generated automatically by the database.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /**
     * The email address of the user.
     * <p>
     * This field maps to the "email" column and must be unique and non-null.
     * </p>
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The hashed password of the user.
     * <p>
     * This field maps to the "password_hash" column and must be non-null.
     * </p>
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * The token used for password reset.
     * <p>
     * This field maps to the "reset_token" column. It can be null if not applicable.
     * </p>
     */
    @Column
    private String resetToken;

    /**
     * The expiry timestamp for the reset token.
     * <p>
     * This field maps to the "reset_token_expiry" column. It can be null if not applicable.
     * </p>
     */
    @Column
    private LocalDateTime resetTokenExpiry;

    /**
     * The timestamp of the last login by the user.
     * <p>
     * This field maps to the "last_login" column. It can be null if the user has not logged in yet.
     * </p>
     */
    @Column
    private LocalDateTime lastLogin;

    /**
     * The timestamp when the user record was created.
     * <p>
     * This field maps to the "created_at" column and is automatically set to the current timestamp when the record is created.
     * It cannot be updated once set.
     * </p>
     */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * The timestamp when the user record was last updated.
     * <p>
     * This field maps to the "updated_at" column and is automatically set to the current timestamp each time the record is updated.
     * </p>
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * The list of categories associated with the user.
     * <p>
     * This field is mapped by the "user" field in the {@link Category} class.
     * It is a one-to-many relationship, where changes to the user will cascade to associated categories.
     * </p>
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories;

    /**
     * The settings specific to the user.
     * <p>
     * This field is mapped by the "user" field in the {@link UserSettings} class.
     * It is a one-to-one relationship, where changes to the user will cascade to associated settings.
     * </p>
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserSettings settings;

    /**
     * Called before the entity is persisted.
     * <p>
     * Sets the "createdAt" and "updatedAt" fields to the current timestamp.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Called before the entity is updated.
     * <p>
     * Sets the "updatedAt" field to the current timestamp.
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


