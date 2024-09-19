package ua.kostenko.recollector.app.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.exception.*;
import ua.kostenko.recollector.app.util.ResponseHelper;

/**
 * Handles exceptions thrown by REST controllers and provides appropriate HTTP responses.
 * This class is annotated with {@code @RestControllerAdvice} to handle exceptions globally
 * across all REST controllers in the application.
 */
@Slf4j
@RestControllerAdvice
public class CustomRestExceptionHandler {

    /**
     * Handles exceptions related to bad requests, such as validation errors and malformed request bodies.
     *
     * @param ex      the exception to handle
     * @param request the HTTP request during which the exception occurred
     *
     * @return a {@code ResponseEntity} containing the error response and {@code 400 Bad Request} status
     */
    @ExceptionHandler({UserCredentialsValidationException.class,
                       UserRegistrationException.class,
                       UserResetPasswordRequiredValuesException.class,
                       CategoryValidationException.class,
                       CategoryAlreadyExistsException.class,
                       ItemAlreadyExistsException.class,
                       ItemValidationException.class,
                       UserSettingsValidationException.class,
                       UserChangePasswordException.class,
                       UserAccountDeleteException.class,
                       IllegalSpecificationParamException.class,
                       HttpMessageNotReadableException.class,
                       IllegalArgumentException.class,
                       MethodArgumentNotValidException.class,
                       DataIntegrityViolationException.class,
                       ConstraintViolationException.class})
    public ResponseEntity<Response<Object>> handleBadRequestException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions related to unauthorized access, such as failed login attempts or invalid JWT tokens.
     *
     * @param ex      the exception to handle
     * @param request the HTTP request during which the exception occurred
     *
     * @return a {@code ResponseEntity} containing the error response and {@code 401 Unauthorized} status
     */
    @ExceptionHandler({UserLoginException.class, UserNotAuthenticatedException.class, JwtTokenException.class})
    public ResponseEntity<Response<Object>> handleUnauthorizedException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles exceptions related to access denial, such as insufficient permissions.
     *
     * @param ex      the exception to handle
     * @param request the HTTP request during which the exception occurred
     *
     * @return a {@code ResponseEntity} containing the error response and {@code 403 Forbidden} status
     */
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Response<Object>> handleAccessDeniedException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles exceptions where requested resources are not found.
     *
     * @param ex      the exception to handle
     * @param request the HTTP request during which the exception occurred
     *
     * @return a {@code ResponseEntity} containing the error response and {@code 404 Not Found} status
     */
    @ExceptionHandler({UserNotFoundException.class,
                       CategoryNotFoundException.class,
                       ItemNotFoundException.class,
                       EntityNotFoundException.class})
    public ResponseEntity<Response<Object>> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions related to too many requests, such as rate limiting for password reset requests.
     *
     * @param ex      the exception to handle
     * @param request the HTTP request during which the exception occurred
     *
     * @return a {@code ResponseEntity} containing the error response and {@code 429 Too Many Requests} status
     */
    @ExceptionHandler(UserForgotPasswordTooManyRequestsException.class)
    public ResponseEntity<Response<Object>> handleTooManyRequestsException(
            UserForgotPasswordTooManyRequestsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.TOO_MANY_REQUESTS);
    }

    /**
     * Handles cases where no handler is found for a given URL.
     *
     * @param ex      the exception to handle
     * @param request the HTTP request during which the exception occurred
     *
     * @return a {@code ResponseEntity} containing the error response and {@code 404 Not Found} status
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                          HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles generic exceptions that do not fall into other categories.
     *
     * @param ex      the exception to handle
     * @param request the HTTP request during which the exception occurred
     *
     * @return a {@code ResponseEntity} containing the error response and {@code 500 Internal Server Error} status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles authentication exceptions, specifically for unauthorized access.
     *
     * @param ex the exception to handle
     *
     * @return a {@code ResponseEntity} containing the error message and {@code 401 Unauthorized} status
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Constructs an error response with the given status and exception details.
     *
     * @param ex      the exception to include in the error response
     * @param request the HTTP request during which the exception occurred
     * @param status  the HTTP status code to set for the response
     *
     * @return a {@code ResponseEntity} containing the error response
     */
    private ResponseEntity<Response<Object>> buildErrorResponse(Exception ex, HttpServletRequest request,
                                                                HttpStatus status) {
        log.warn(ex.getMessage(), ex);
        var requestBody = request.getAttribute("requestBody");
        return ResponseHelper.buildDtoErrorResponse(requestBody, status, ex);
    }
}
