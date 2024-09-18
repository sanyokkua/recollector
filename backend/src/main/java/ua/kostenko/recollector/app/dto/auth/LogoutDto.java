package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for logging out a user.
 * Contains the email of the user who is requesting to log out.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for logging out a user.")
public class LogoutDto {

    @Schema(description = "Email of the user requesting to log out.", example = "user@example.com")
    private String userEmail;
}
