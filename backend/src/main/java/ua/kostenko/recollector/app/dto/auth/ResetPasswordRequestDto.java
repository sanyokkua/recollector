package ua.kostenko.recollector.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {

    private String email;
    private String password;
    private String passwordConfirm;
    private String passwordResetToken;
}
