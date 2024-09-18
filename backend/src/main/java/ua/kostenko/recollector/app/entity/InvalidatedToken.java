package ua.kostenko.recollector.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing a token that has been invalidated.
 * <p>
 * This entity is mapped to the 'invalidated_tokens' table in the 'recollector' schema.
 * It holds information about the invalidated token, its expiration time, and the associated user.
 * </p>
 *
 * <p>Includes JPA annotations for persistence management and lifecycle callbacks for timestamps.</p>
 */
@Entity
@Table(name = "invalidated_tokens", schema = "recollector")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvalidatedToken {

    /**
     * Unique identifier for the invalidated token.
     * <p>
     * Maps to the "token_id" column in the "invalidated_tokens" table. This field is the primary key and is generated automatically.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    /**
     * The user associated with the invalidated token.
     * <p>
     * Maps to the "user_id" column. This field establishes a many-to-one relationship with the {@link User} entity.
     * The relationship is mandatory, indicating that every invalidated token must be associated with a user.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The token that has been invalidated.
     * <p>
     * Maps to the "token" column. This field is mandatory and holds the token string.
     * The length is set to 1024 characters to accommodate JWT tokens and other long tokens.
     * </p>
     */
    @Column(nullable = false, length = 1024)
    private String token;

    /**
     * The expiration time of the token.
     * <p>
     * Maps to the "expires_at" column. This field is mandatory and stores the timestamp when the token is set to expire.
     * </p>
     */
    @Column(nullable = false, name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * The timestamp when the token was invalidated.
     * <p>
     * Maps to the "invalidated_at" column. This field is automatically set when the record is created and cannot be updated.
     * </p>
     */
    @Column(nullable = false, name = "invalidated_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime invalidatedAt;

    /**
     * Sets the invalidation timestamp before persisting the entity.
     * <p>
     * Called before the entity is persisted to ensure "invalidatedAt" is set.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        this.invalidatedAt = LocalDateTime.now();
    }
}
