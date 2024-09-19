package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Data Transfer Object for holding JWT tokens and their expiration details.
 * Contains the access token, refresh token, and their respective expiration dates.
 */
@Data
@Builder
@RequiredArgsConstructor
@Schema(description = "Data Transfer Object for holding JWT tokens and their expiration details.")
public class TokensDto {

    @Schema(description = "JWT access token used for authentication.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String jwtToken;

    @Schema(description = "JWT refresh token used to obtain a new access token.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String refreshToken;

    @Schema(description = "Expiration date of the JWT access token in seconds since epoch.", example = "1672531199")
    private final int jwtTokenExpirationDate;

    @Schema(description = "Expiration date of the JWT refresh token in seconds since epoch.", example = "1672534799")
    private final int jwtRefreshTokenExpirationDate;
}

