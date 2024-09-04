package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for resetting a user's password.
 * Contains the email, new password, confirmation of the new password, and a password reset token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for resetting a user's password.")
public class ResetPasswordRequestDto {

    @Schema(description = "Email of the user requesting the password reset.", example = "user@example.com")
    private String email;

    @Schema(description = "New password for the user account.", example = "newPassword123")
    private String password;

    @Schema(description = "Confirmation of the new password to ensure it matches.", example = "newPassword123")
    private String passwordConfirm;

    @Schema(description = "Token used to verify the password reset request.", example = "abc123xyz")
    private String passwordResetToken;
}
