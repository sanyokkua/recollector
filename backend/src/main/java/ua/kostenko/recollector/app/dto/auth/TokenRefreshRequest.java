package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for requesting a token refresh.
 * Contains the email of the user for whom the access token is being refreshed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for requesting a token refresh.")
public class TokenRefreshRequest {

    @Schema(description = "Email of the user requesting the token refresh.", example = "user@example.com")
    private String userEmail;
    @Schema(description = "JWT access token used for authentication.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
}

