package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration.
 * Contains the email, password, and password confirmation needed for user registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for user registration.")
public class RegisterRequestDto {

    @Schema(description = "Email of the user registering for the application.", example = "user@example.com")
    private String email;

    @Schema(description = "Password for the user account.", example = "newPassword123")
    private String password;

    @Schema(description = "Confirmation of the password to ensure it matches.", example = "newPassword123")
    private String passwordConfirm;
}