package ua.kostenko.recollector.app.exceptions;

public class UserCredentialsValidationException extends RuntimeException {

    public UserCredentialsValidationException(String message) {
        super(message);
    }
}
