package ua.kostenko.recollector.app.exceptions;

import java.util.List;

public class UserRegistrationException extends RuntimeException {

    public UserRegistrationException(String message) {
        super(message);
    }

    public UserRegistrationException(List<String> errors) {
        super(String.join(", ", errors));
    }
}
