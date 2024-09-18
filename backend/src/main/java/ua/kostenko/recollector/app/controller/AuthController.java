package ua.kostenko.recollector.app.controller;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.dto.auth.*;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.security.AuthenticationService;
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

    private final AuthenticationService authService;

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
        log.info("User with email {} successfully registered", requestDto.getEmail());
        return ResponseHelper.buildDtoResponse(registeredUser, HttpStatus.CREATED);
    }

    /**
     * Logs in an existing user.
     *
     * @param requestDto the login request containing email and password.
     *
     * @return a {@link ResponseEntity} with the user's details, JWT token, and HTTP status {@code 200 OK}.
     */
    @PostMapping("/login")
    @Operation(summary = "Login an existing user", description = "Authenticates a user with the provided email and password, and returns a JWT token.")
    public ResponseEntity<Response<UserDto>> loginUser(
            @RequestBody @Parameter(description = "User credentials for login") LoginRequestDto requestDto) {
        log.info("Attempting to authenticate user with email: {}", requestDto.getEmail());
        var auth = authService.loginUser(requestDto.getEmail(), requestDto.getPassword());
        var email = auth.getUserEmail();
        var jwt = auth.getTokensDto().getJwtToken();
        var jwtRefresh = auth.getTokensDto().getRefreshToken();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken")
                                                     .value(jwtRefresh)
                                                     .httpOnly(true)
                                                     .secure(false) // Production should use true
                                                     .path("api/v1/auth/refresh-token")
                                                     .maxAge(auth.getTokensDto().getJwtRefreshTokenExpirationDate())
                                                     .build();

        UserDto userDto = UserDto.builder().email(email).jwtToken(jwt).build();
        log.info("User with email {} successfully authenticated", requestDto.getEmail());
        return ResponseHelper.buildDtoResponse(userDto, HttpStatus.OK, refreshCookie);
    }

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param requestDto   the token refresh request containing user email.
     * @param refreshToken the refresh token from cookies.
     *
     * @return a {@link ResponseEntity} with the new access token and user details.
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Refreshes the access token using the refresh token.")
    public ResponseEntity<Response<UserDto>> refreshToken(
            @RequestBody @Parameter(description = "Token refresh request containing user email") TokenRefreshRequest requestDto,
            @CookieValue("refreshToken") @Parameter(description = "Refresh token stored in a cookie") String refreshToken) {
        log.info("Refreshing access token for user with email: {}", requestDto.getUserEmail());
        var token = requestDto.getAccessToken();
        var userDto = authService.refreshAccessToken(requestDto.getUserEmail(), token, refreshToken);
        log.info("Access token refreshed for user with email: {}", requestDto.getUserEmail());
        return ResponseHelper.buildDtoResponse(userDto, HttpStatus.OK);
    }

    /**
     * Logs out a user by invalidating the access and refresh tokens.
     *
     * @param requestDto          the logout request containing user email.
     * @param authorizationHeader the authorization header containing the access token.
     * @param refreshToken        the refresh token from cookies.
     *
     * @return a {@link ResponseEntity} indicating successful logout.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the user by invalidating the access and refresh tokens.")
    public ResponseEntity<Response<String>> logoutUser(
            @RequestBody @Parameter(description = "Details of the user requesting logout") LogoutDto requestDto,
            @RequestHeader("Authorization") @Parameter(description = "Authorization header containing access token") String authorizationHeader,
            @CookieValue("refreshToken") @Parameter(description = "Refresh token stored in a cookie") String refreshToken) {
        log.info("Logging out user with email: {}", requestDto.getUserEmail());
        var token = getTokenFromAuthHeader(authorizationHeader);
        var result = authService.logoutUser(requestDto.getUserEmail(), token, refreshToken);
        log.info("User with email {} successfully logged out", requestDto.getUserEmail());
        return ResponseHelper.buildDtoResponse(result, HttpStatus.OK);
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
            @RequestBody @Parameter(description = "Old and new password details") ChangePasswordRequestDto requestDto,
            @RequestHeader("Authorization") @Parameter(description = "Authorization header containing access token") String authorizationHeader,
            @CookieValue("refreshToken") @Parameter(description = "Refresh token stored in a cookie") String refreshToken) {
        log.info("Changing password for user with email: {}", requestDto.getEmail());
        var token = getTokenFromAuthHeader(authorizationHeader);
        var user = authService.changePassword(requestDto, token, refreshToken);
        log.info("Password changed for user with email: {}", requestDto.getEmail());
        return ResponseHelper.buildDtoResponse(user, HttpStatus.OK);
    }

    /**
     * Initiates a password reset process.
     *
     * @param requestDto the request containing user email for password reset.
     *
     * @return a {@link ResponseEntity} with a message indicating that the password reset link was sent.
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset", description = "Sends a password reset link to the email provided in the request.")
    public ResponseEntity<Response<String>> forgotPassword(
            @RequestBody @Parameter(description = "Email of the user requesting password reset") ForgotPasswordRequestDto requestDto) {
        log.info("Processing password reset request for email: {}", requestDto.getEmail());
        authService.forgotPassword(requestDto);
        log.info("Password reset link sent to email: {}", requestDto.getEmail());
        return ResponseHelper.buildDtoResponse("Password reset link sent", HttpStatus.OK);
    }

    /**
     * Resets the user's password.
     *
     * @param requestDto the request containing new password details.
     *
     * @return a {@link ResponseEntity} with the updated user details.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password", description = "Resets the user's password with the new password provided in the request.")
    public ResponseEntity<Response<UserDto>> resetPassword(
            @RequestBody @Parameter(description = "New password details") ResetPasswordRequestDto requestDto) {
        log.info("Resetting password for user with email: {}", requestDto.getEmail());
        var user = authService.resetPassword(requestDto);
        log.info("Password reset successfully for user with email: {}", requestDto.getEmail());
        return ResponseHelper.buildDtoResponse(user, HttpStatus.OK);
    }

    /**
     * Deletes a user account.
     *
     * @param requestDto the request containing user details for account deletion.
     *
     * @return a {@link ResponseEntity} with a message indicating the result of the deletion.
     */
    @PostMapping("/delete-account")
    @Operation(summary = "Delete user account", description = "Deletes the user account specified in the request. The user must be authenticated to perform this action.")
    public ResponseEntity<Response<String>> deleteAccount(
            @RequestBody @Parameter(description = "Details of the user account to be deleted") AccountDeleteRequestDto requestDto) {
        log.info("Deleting account for user with email: {}", requestDto.getEmail());
        var result = authService.deleteAccount(requestDto);
        log.info("Account deleted for user with email: {}", requestDto.getEmail());
        return ResponseHelper.buildDtoResponse(result, HttpStatus.OK);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param authHeader the authorization header containing the JWT token.
     *
     * @return the extracted token, or an empty string if the header is blank.
     */
    private String getTokenFromAuthHeader(String authHeader) {
        if (StringUtils.isBlank(authHeader)) {
            log.warn("Authorization header is blank or missing");
            return "";
        }
        return authHeader.substring(7); // Extract token part
    }
}
