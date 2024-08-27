package ua.kostenko.recollector.app.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.exception.*;
import ua.kostenko.recollector.app.util.ResponseHelper;

@Slf4j
@RestControllerAdvice
public class CustomRestExceptionHandler {

    @ExceptionHandler({UserCredentialsValidationException.class,
                       UserRegistrationException.class,
                       UserResetPasswordRequiredValuesException.class,
                       CategoryValidationException.class,
                       CategoryAlreadyExistsException.class,
                       ItemAlreadyExistsException.class,
                       ItemValidationException.class,
                       UserChangePasswordException.class,
                       IllegalSpecificationParamException.class})
    public ResponseEntity<Response<Object>> handleBadRequestException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserLoginException.class, UserNotAuthenticatedException.class})
    public ResponseEntity<Response<Object>> handleUnauthorizedException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class, ItemNotFoundException.class})
    public ResponseEntity<Response<Object>> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserForgotPasswordTooManyRequestsException.class)
    public ResponseEntity<Response<Object>> handleTooManyRequestsException(
            UserForgotPasswordTooManyRequestsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Response<Object>> buildErrorResponse(Exception ex, HttpServletRequest request,
                                                                HttpStatus status) {
        log.warn(ex.getMessage(), ex);
        var requestBody = request.getAttribute("requestBody");
        return ResponseHelper.buildDtoErrorResponse(requestBody, status, ex);
    }
}
