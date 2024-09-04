package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for account deletion request.
 * Contains the email and password information required to delete an account.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for account deletion request.")
public class AccountDeleteRequestDto {

    @Schema(description = "Email of the user requesting account deletion.", example = "user@example.com")
    private String email;

    @Schema(description = "Password of the user for account verification.", example = "securePassword123")
    private String password;

    @Schema(description = "Confirmation of the password for account verification.", example = "securePassword123")
    private String passwordConfirm;
}
