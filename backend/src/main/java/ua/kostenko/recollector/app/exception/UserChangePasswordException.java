package ua.kostenko.recollector.app.exception;

import java.util.List;

public class UserChangePasswordException extends RuntimeException {

    public UserChangePasswordException(String message) {
        super(message);
    }

    public UserChangePasswordException(List<String> errors) {
        super(String.join(", ", errors));
    }
}
