package ua.kostenko.recollector.app.exception;

import java.util.List;

public class UserAccountDeleteException extends RuntimeException {

    public UserAccountDeleteException(String message) {
        super(message);
    }

    public UserAccountDeleteException(List<String> errors) {
        super(String.join(", ", errors));
    }
}
