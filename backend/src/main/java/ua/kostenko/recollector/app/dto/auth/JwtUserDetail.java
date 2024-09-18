package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for containing user details with JWT tokens.
 * Includes the user's email and their associated JWT tokens.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for containing user details with JWT tokens.")
public class JwtUserDetail {

    @Schema(description = "Email of the user.", example = "user@example.com")
    private String userEmail;

    @Schema(description = "JWT tokens and their expiration details associated with the user.")
    private TokensDto tokensDto;
}

