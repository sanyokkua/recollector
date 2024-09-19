package ua.kostenko.recollector.app.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.dto.auth.*;
import ua.kostenko.recollector.app.entity.InvalidatedToken;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.entity.UserSettings;
import ua.kostenko.recollector.app.exception.*;
import ua.kostenko.recollector.app.repository.InvalidatedTokenRepository;
import ua.kostenko.recollector.app.repository.UserRepository;
import ua.kostenko.recollector.app.util.UserUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * AuthenticationService handles user authentication, registration, login, and password management,
 * including password reset and forgot password functionality. It integrates with Spring Security.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserUtils userUtils;
    private final JwtHelperUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final InvalidatedTokenRepository tokenRepository;

    @Value("${recollector.app.jwt.secret.exp}")
    private Integer jwtExpMinutes;

    @Value("${recollector.app.jwt.refresh.exp}")
    private Integer jwtRefreshExpHours;

    /**
     * Authenticates a user by email and password and returns a JWT token pair.
     *
     * @param email    the email of the user
     * @param password the password of the user
     *
     * @return JwtUserDetail containing the user's email and tokens
     *
     * @throws UserCredentialsValidationException if email or password is invalid
     * @throws UserNotFoundException              if the user is not found
     * @throws UserLoginException                 if the password is incorrect
     */
    public JwtUserDetail loginUser(String email, String password) {
        validateEmail(email);
        validatePassword(password);

        User user = findUserByEmail(email);
        verifyPasswordWithDbHash(password, user);

        var tokensDto = jwtUtil.generateJwtTokensPair(email);
        updateLastLogin(user);
        log.info("User '{}' logged in successfully", email);
        return JwtUserDetail.builder().userEmail(email).tokensDto(tokensDto).build();
    }

    /**
     * Registers a new user with the provided registration details.
     *
     * @param requestDto the registration request data
     *
     * @return UserDto with user details
     *
     * @throws UserRegistrationException if the email is already registered
     */
    public UserDto registerUser(RegisterRequestDto requestDto) {
        userUtils.validateRegisterRequestDto(requestDto);

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            String message = String.format("Email '%s' is already registered", requestDto.getEmail());
            log.error(message);
            throw new UserRegistrationException(message);
        }

        User newUser = User.builder()
                           .email(requestDto.getEmail())
                           .passwordHash(passwordEncoder.encode(requestDto.getPassword()))
                           .lastLogin(LocalDateTime.now())
                           .build();

        UserSettings settings = createDefaultUserSettings(newUser);
        newUser.setSettings(settings);

        return saveUserAndReturnDto(newUser, "User Registration");
    }

    /**
     * Refreshes the access token using a refresh token.
     *
     * @param userEmail    the email of the user
     * @param mainToken    the current main JWT token
     * @param refreshToken the refresh JWT token
     *
     * @return UserDto with a new JWT token
     *
     * @throws UserLoginException if the refresh token is expired
     */
    public UserDto refreshAccessToken(String userEmail, String mainToken, String refreshToken) {
        if (!jwtUtil.validateRefreshJwtToken(refreshToken, userEmail)) {
            log.warn("Invalid refresh token for user '{}'", userEmail);
            return UserDto.builder().email(userEmail).jwtToken("").build();
        }

        var user = findUserByEmail(userEmail);
        var invalidatedToken = tokenRepository.findByUser_UserIdAndToken(user.getUserId(), refreshToken);

        if (invalidatedToken.isPresent()) {
            logoutUser(userEmail, mainToken, refreshToken);
            throw new UserLoginException("Refresh token expired");
        }

        invalidateMainToken(user, mainToken);

        String newToken = jwtUtil.generateMainJwt(userEmail, Date.from(Instant.now()));
        return UserDto.builder().email(userEmail).jwtToken(newToken).build();
    }

    /**
     * Logs out a user by invalidating their tokens.
     *
     * @param email        the email of the user
     * @param mainToken    the main JWT token
     * @param refreshToken the refresh JWT token
     *
     * @return a success message
     */
    public String logoutUser(String email, String mainToken, String refreshToken) {
        validateEmail(email);
        User user = findUserByEmail(email);
        invalidateMainToken(user, mainToken);
        invalidateRefreshToken(user, refreshToken);
        log.info("User '{}' logged out successfully", email);
        return "Logout successful";
    }

    /**
     * Changes the password for a user.
     *
     * @param requestDto   the change password request data
     * @param mainToken    the main JWT token
     * @param refreshToken the refresh JWT token
     *
     * @return UserDto with updated user details
     *
     * @throws UserChangePasswordException if the new password is the same as the old password
     */
    public UserDto changePassword(ChangePasswordRequestDto requestDto, String mainToken, String refreshToken) {
        userUtils.validateChangePasswordRequestDto(requestDto);

        User user = findUserByEmail(requestDto.getEmail());
        verifyPasswordWithDbHash(requestDto.getPasswordCurrent(), user);
        validatePasswordAlreadyInUse(requestDto.getPassword(), user);

        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        invalidateMainToken(user, mainToken);
        invalidateRefreshToken(user, refreshToken);

        return saveUserAndReturnDto(user, "password changed");
    }

    /**
     * Initiates the password recovery process by generating a reset token.
     *
     * @param requestDto the forgot password request data
     */
    public void forgotPassword(ForgotPasswordRequestDto requestDto) {
        try {
            User user = findUserByEmail(requestDto.getEmail());
            checkResetTokenAvailability(user);
            generateAndSaveResetToken(user);
            log.info("Password reset token generated for email '{}'", requestDto.getEmail());
        } catch (Exception ex) {
            log.warn("Password recovery attempt failed for email '{}'", requestDto.getEmail(), ex);
        }
    }

    /**
     * Resets the user's password using the provided reset token.
     *
     * @param resetRequest the reset password request data
     *
     * @return UserDto with updated user details
     *
     * @throws UserResetPasswordRequiredValuesException if the reset token is invalid
     */
    public UserDto resetPassword(ResetPasswordRequestDto resetRequest) {
        userUtils.validateResetPasswordRequestDto(resetRequest);
        User user = findUserByEmail(resetRequest.getEmail());
        validateResetToken(user, resetRequest);
        validatePasswordAlreadyInUse(resetRequest.getPassword(), user);

        user.setPasswordHash(passwordEncoder.encode(resetRequest.getPassword()));
        clearResetToken(user);

        return saveUserAndReturnDto(user, "password reset");
    }

    /**
     * Deletes a user account.
     *
     * @param requestDto the account delete request data
     *
     * @return a success message
     *
     * @throws UserRegistrationException if the user is not found or password is incorrect
     */
    public String deleteAccount(AccountDeleteRequestDto requestDto) {
        userUtils.validateAccountDeleteRequestDto(requestDto);

        User user = findUserByEmail(requestDto.getEmail());
        verifyPasswordWithDbHash(requestDto.getPassword(), user);

        userRepository.delete(user);
        log.info("Account '{}' deleted successfully", requestDto.getEmail());
        return "Successfully deleted account '" + requestDto.getEmail() + "'";
    }

    /**
     * Retrieves the user from the authentication context.
     *
     * @return the authenticated user
     *
     * @throws UserNotAuthenticatedException if no user is authenticated
     */
    @NonNull
    public User getUserFromAuthContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User is not authenticated");
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        User userFromContext = (User) authentication.getPrincipal();
        return findUserByEmail(userFromContext.getEmail());
    }

    /**
     * Validates if the provided new password is not the same as the current password.
     *
     * @param newPassword the new password to be validated
     * @param user        the user whose current password is to be checked
     *
     * @throws UserChangePasswordException if the new password is the same as the current password
     */
    public void validatePasswordAlreadyInUse(String newPassword, User user) {
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            log.warn("Attempted to change to a password already in use for user '{}'", user.getEmail());
            throw new UserChangePasswordException("Password already in use. Create a brand new password");
        }
    }

    /**
     * Finds a user by email.
     *
     * @param email the email of the user
     *
     * @return the user
     *
     * @throws UserNotFoundException if the user is not found
     */
    public User findUserByEmail(String email) {
        validateEmail(email);
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("User with email '{}' not found", email);
            return new UserNotFoundException("User with email '" + email + "' not found");
        });
    }

    private UserSettings createDefaultUserSettings(User user) {
        return UserSettings.builder()
                           .user(user)
                           .categoryBackgroundColor(UserSettings.DEFAULT_CATEGORY_BACKGROUND_COLOR)
                           .categoryItemColor(UserSettings.DEFAULT_CATEGORY_ITEM_COLOR)
                           .categoryFabColor(UserSettings.DEFAULT_CATEGORY_FAB_COLOR)
                           .categoryPageSize(UserSettings.DEFAULT_CATEGORY_PAGE_SIZE)
                           .itemBackgroundColor(UserSettings.DEFAULT_ITEM_BACKGROUND_COLOR)
                           .itemItemColor(UserSettings.DEFAULT_ITEM_ITEM_COLOR)
                           .itemFabColor(UserSettings.DEFAULT_ITEM_FAB_COLOR)
                           .itemPageSize(UserSettings.DEFAULT_ITEM_PAGE_SIZE)
                           .build();
    }

    private void invalidateMainToken(User user, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(jwtExpMinutes);
        try {
            var tokenExp = jwtUtil.extractClaimsFromMainJwtToken(token).getExpiration();
            expiresAt = tokenExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (RuntimeException e) {
            log.warn("Failed to extract expiration datetime from main token. Default value used.");
        }

        saveInvalidatedToken(user, token, expiresAt);
    }

    private void invalidateRefreshToken(User user, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(jwtRefreshExpHours);
        try {
            var tokenExp = jwtUtil.extractClaimsFromRefreshJwtToken(token).getExpiration();
            expiresAt = tokenExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (RuntimeException e) {
            log.warn("Failed to extract expiration datetime from refresh token. Default value used.");
        }

        saveInvalidatedToken(user, token, expiresAt);
    }

    private void saveInvalidatedToken(User user, String token, LocalDateTime expiresAt) {
        log.debug("TRY to invalidate: {}", token);
        var foundInDb = tokenRepository.findByUser_UserIdAndToken(user.getUserId(), token);
        if (foundInDb.isEmpty()) {
            log.debug("NOT FOUND IN DB: {}", token);
            var invalidatedToken = InvalidatedToken.builder()
                                                   .user(user)
                                                   .token(token)
                                                   .expiresAt(expiresAt)
                                                   .invalidatedAt(LocalDateTime.now())
                                                   .build();
            tokenRepository.saveAndFlush(invalidatedToken);
        }
    }

    private void validateEmail(String email) {
        if (!userUtils.isEmailValid(email)) {
            log.warn("Invalid email format: {}", email);
            throw new UserCredentialsValidationException("Email has wrong format");
        }
    }

    private void validatePassword(String password) {
        if (!userUtils.isPasswordValid(password)) {
            log.warn("Invalid password format: {}", password);
            throw new UserCredentialsValidationException("Password has wrong format");
        }
    }

    private void verifyPasswordWithDbHash(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Incorrect password for user '{}'", user.getEmail());
            throw new UserLoginException("Wrong password");
        }
    }

    private void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.saveAndFlush(user);
        log.info("Updated last login time for user '{}'", user.getEmail());
    }

    private void checkResetTokenAvailability(User user) {
        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(user.getResetToken()) && Objects.nonNull(user.getResetTokenExpiry()) && user.getResetTokenExpiry()
                                                                                                        .isAfter(now)) {
            long timeToWait = ChronoUnit.MINUTES.between(now, user.getResetTokenExpiry());
            log.warn("Too many password recovery requests. Time to wait: {} minutes", timeToWait);
            throw new UserForgotPasswordTooManyRequestsException(
                    "Too many requests for password restore. You need to wait: " + timeToWait + " minutes");
        }
    }

    private void generateAndSaveResetToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.saveAndFlush(user);
        log.info("Reset token generated and saved for user '{}'", user.getEmail());
    }

    private void validateResetToken(User user, ResetPasswordRequestDto resetRequest) {
        if (StringUtils.isBlank(resetRequest.getPasswordResetToken())) {
            log.warn("Reset token is empty for email: {}", resetRequest.getEmail());
            throw new UserResetPasswordRequiredValuesException("Password reset token is empty");
        }
        if (!resetRequest.getPasswordResetToken().equals(user.getResetToken())) {
            log.warn("Reset token does not match for email: {}", resetRequest.getEmail());
            throw new UserResetPasswordRequiredValuesException("Password reset token does not match");
        }
    }

    private void clearResetToken(User user) {
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.saveAndFlush(user);
        log.info("Reset token cleared for user '{}'", user.getEmail());
    }

    private UserDto saveUserAndReturnDto(User user, String action) {
        try {
            User savedUser = userRepository.save(user);
            log.info("User with email '{}' {} successfully", savedUser.getEmail(), action);
            return UserDto.builder().email(savedUser.getEmail()).build();
        } catch (Exception ex) {
            log.error("Failed to {} user with email '{}': {}", action, user.getEmail(), ex.getMessage(), ex);
            throw new UserRegistrationException(ex.getMessage());
        }
    }
}
