package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for changing the user's password.
 * Contains fields for the current password and the new password along with confirmation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for changing the user's password.")
public class ChangePasswordRequestDto {

    @Schema(description = "Email of the user requesting the password change.", example = "user@example.com")
    private String email;

    @Schema(description = "Current password of the user for authentication.", example = "currentPassword123")
    private String passwordCurrent;

    @Schema(description = "New password that the user wants to set.", example = "newSecurePassword456")
    private String password;

    @Schema(description = "Confirmation of the new password to ensure it matches.", example = "newSecurePassword456")
    private String passwordConfirm;
}