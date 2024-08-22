package ua.kostenko.recollector.app.exceptions;

public class UserForgotPasswordTooManyRequestsException extends RuntimeException {

    public UserForgotPasswordTooManyRequestsException(String message) {
        super(message);
    }
}
