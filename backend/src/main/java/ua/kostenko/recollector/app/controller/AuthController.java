package ua.kostenko.recollector.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.dto.auth.*;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.util.ResponseHelper;

/**
 * REST controller for handling authentication and user account operations.
 * Provides endpoints for user registration, login, password management, and account deletion.
 */
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication REST Controller", description = "Handles user registration, login, password management, and account deletion.")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     *
     * @param requestDto the registration request containing user details.
     *
     * @return a {@link ResponseEntity} with the registered user details and HTTP status {@code 201 Created}.
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user by providing user details such as email and password.")
    public ResponseEntity<Response<UserDto>> registerUser(
            @RequestBody @Parameter(description = "Details of the user to be registered") RegisterRequestDto requestDto) {
        log.info("Registering new user with email: {}", requestDto.getEmail());
        var registeredUser = authService.registerUser(requestDto);
        return ResponseHelper.buildDtoResponse(registeredUser, HttpStatus.CREATED);
    }

    /**
     * Logs in an existing user.
     *
     * @param requestDto the login request containing email and password.
     *
     * @return a {@link ResponseEntity} with the user's details and JWT token, and HTTP status {@code 200 OK}.
     */
    @PostMapping("/login")
    @Operation(summary = "Login an existing user", description = "Authenticates a user with the provided email and password, and returns a JWT token.")
    public ResponseEntity<Response<UserDto>> loginUser(
            @RequestBody @Parameter(description = "User credentials for login") LoginRequestDto requestDto) {
        log.info("Attempting to authenticate user with email: {}", requestDto.getEmail());
        var authentication = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        var auth = authService.authenticate(authentication);
        var email = auth.getPrincipal() + "";
        var jwt = auth.getCredentials() + "";
        var userDto = UserDto.builder().email(email).jwtToken(jwt).build();

        return ResponseHelper.buildDtoResponse(userDto, HttpStatus.OK);
    }

    /**
     * Initiates a password reset process.
     *
     * @param requestDto the request containing user email for password reset.
     *
     * @return a {@link ResponseEntity} with a message indicating that the password reset link was sent, and HTTP status {@code 200 OK}.
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset", description = "Sends a password reset link to the email provided in the request.")
    public ResponseEntity<Response<String>> forgotPassword(
            @RequestBody @Parameter(description = "Email of the user requesting password reset") ForgotPasswordRequestDto requestDto) {
        log.info("Processing password reset request for email: {}", requestDto.getEmail());
        authService.forgotPassword(requestDto);
        return ResponseHelper.buildDtoResponse("Password reset link sent", HttpStatus.OK);
    }

    /**
     * Resets the user's password.
     *
     * @param requestDto the request containing new password details.
     *
     * @return a {@link ResponseEntity} with the updated user details and HTTP status {@code 200 OK}.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password", description = "Resets the user's password with the new password provided in the request.")
    public ResponseEntity<Response<UserDto>> resetPassword(
            @RequestBody @Parameter(description = "New password details") ResetPasswordRequestDto requestDto) {
        log.info("Resetting password for user with email: {}", requestDto.getEmail());
        var user = authService.resetPassword(requestDto);
        return ResponseHelper.buildDtoResponse(user, HttpStatus.OK);
    }

    /**
     * Changes the user's password.
     *
     * @param requestDto the request containing old and new password details.
     *
     * @return a {@link ResponseEntity} with the updated user details and HTTP status {@code 200 OK}.
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change user password", description = "Changes the user's password from the old one to the new password provided in the request.")
    public ResponseEntity<Response<UserDto>> changePassword(
            @RequestBody @Parameter(description = "Old and new password details") ChangePasswordRequestDto requestDto) {
        log.info("Changing password for user with email: {}", requestDto.getEmail());
        var user = authService.changePassword(requestDto);
        return ResponseHelper.buildDtoResponse(user, HttpStatus.OK);
    }

    /**
     * Deletes a user account.
     *
     * @param requestDto the request containing user details for account deletion.
     *
     * @return a {@link ResponseEntity} with a message indicating the result of the deletion and HTTP status {@code 200 OK}.
     */
    @PostMapping("/delete-account")
    @Operation(summary = "Delete user account", description = "Deletes the user account specified in the request. The user must be authenticated to perform this action.")
    public ResponseEntity<Response<String>> deleteAccount(
            @RequestBody @Parameter(description = "Details of the user account to be deleted") AccountDeleteRequestDto requestDto) {
        log.info("Deleting account for user with email: {}", requestDto.getEmail());
        var result = authService.deleteAccount(requestDto);
        return ResponseHelper.buildDtoResponse(result, HttpStatus.OK);
    }
}
