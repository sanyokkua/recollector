package ua.kostenko.recollector.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user details.
 * Includes the user's email and JWT token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for user details, including email and JWT token.")
public class UserDto {

    @Schema(description = "The email address of the user.", example = "user@example.com")
    private String email;

    @Schema(description = "(Only in Response) The JWT token for the user.", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImlhdCI6MTcyNTQ2NzI4OSwiZXhwIjoxNzI1NDcwODg5fQ.EPjw6gHZcqjxplXel1fvxoSZYpCoMy8zJI-hLzYggtk")
    private String jwtToken;
}