package ua.kostenko.recollector.app.exception;

public class JwtTokenException extends RuntimeException {

    public JwtTokenException(String message) {
        super(message);
    }
}
