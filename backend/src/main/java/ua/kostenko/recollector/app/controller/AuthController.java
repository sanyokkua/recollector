package ua.kostenko.recollector.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    /**
     * Registers a new user.
     *
     * @param requestDto the registration request containing user details.
     *
     * @return a {@link ResponseEntity} with the registered user details and HTTP status {@code 201 Created}.
     */
    @PostMapping("/register")
    public ResponseEntity<Response<UserDto>> registerUser(@RequestBody RegisterRequestDto requestDto) {
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
    public ResponseEntity<Response<UserDto>> loginUser(@RequestBody LoginRequestDto requestDto) {
        log.info("Attempting to authenticate user with email: {}", requestDto.getEmail());
        var authentication = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        var auth = authenticationManager.authenticate(authentication);
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
    public ResponseEntity<Response<String>> forgotPassword(@RequestBody ForgotPasswordRequestDto requestDto) {
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
    public ResponseEntity<Response<UserDto>> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
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
    public ResponseEntity<Response<UserDto>> changePassword(@RequestBody ChangePasswordRequestDto requestDto) {
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
    public ResponseEntity<Response<String>> deleteAccount(@RequestBody AccountDeleteRequestDto requestDto) {
        log.info("Deleting account for user with email: {}", requestDto.getEmail());
        var result = authService.deleteAccount(requestDto);
        return ResponseHelper.buildDtoResponse(result, HttpStatus.OK);
    }
}
