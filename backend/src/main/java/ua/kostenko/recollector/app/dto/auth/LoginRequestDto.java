package ua.kostenko.recollector.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login.
 * Contains the email and password used for user authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for user login.")
public class LoginRequestDto {

    @Schema(description = "Email of the user attempting to log in.", example = "user@example.com")
    private String email;

    @Schema(description = "Password of the user for authentication.", example = "yourPassword123")
    private String password;
}
