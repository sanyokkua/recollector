package ua.kostenko.recollector.app.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.exceptions.*;
import ua.kostenko.recollector.app.util.ResponseHelper;

@RestControllerAdvice
public class CustomRestExceptionHandler {

    @ExceptionHandler({UserCredentialsValidationException.class,
                       UserRegistrationException.class,
                       UserResetPasswordRequiredValuesException.class})
    public ResponseEntity<Response<Object>> handleCustomBadRequestExceptions(Exception ex, HttpServletRequest request) {
        var requestBody = request.getAttribute("requestBody");
        return ResponseHelper.buildDtoErrorResponse(requestBody, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler({UserLoginException.class})
    public ResponseEntity<Response<Object>> handleUserLoginException(UserLoginException ex,
                                                                     HttpServletRequest request) {
        var requestBody = request.getAttribute("requestBody");
        return ResponseHelper.buildDtoErrorResponse(requestBody, HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Response<Object>> handleUserNotFoundException(UserNotFoundException ex,
                                                                        HttpServletRequest request) {
        var requestBody = request.getAttribute("requestBody");
        return ResponseHelper.buildDtoErrorResponse(requestBody, HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Response<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        var requestBody = request.getAttribute("requestBody");
        return ResponseHelper.buildDtoErrorResponse(requestBody, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler({UserForgotPasswordTooManyRequestsException.class})
    public ResponseEntity<Response<Object>> handleTooManyRequests(Exception ex, HttpServletRequest request) {
        var requestBody = request.getAttribute("requestBody");
        return ResponseHelper.buildDtoErrorResponse(requestBody, HttpStatus.TOO_MANY_REQUESTS, ex);
    }
}
