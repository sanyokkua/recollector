package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for requesting a password reset.
 * Contains the email of the user for whom the password reset is requested.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for requesting a password reset.")
public class ForgotPasswordRequestDto {

    @Schema(description = "Email of the user who requested the password reset.", example = "user@example.com")
    private String email;
}