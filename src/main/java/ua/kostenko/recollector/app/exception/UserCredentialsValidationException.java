package ua.kostenko.recollector.app.exception;

public class UserCredentialsValidationException extends RuntimeException {

    public UserCredentialsValidationException(String message) {
        super(message);
    }
}
