package ua.kostenko.recollector.app.exception;

import java.util.List;

public class UserResetPasswordRequiredValuesException extends RuntimeException {

    public UserResetPasswordRequiredValuesException(String message) {
        super(message);
    }

    public UserResetPasswordRequiredValuesException(List<String> errors) {
        super(String.join(", ", errors));
    }
}
