package ua.kostenko.recollector.app.exception;

public class UserForgotPasswordTooManyRequestsException extends RuntimeException {

    public UserForgotPasswordTooManyRequestsException(String message) {
        super(message);
    }
}
