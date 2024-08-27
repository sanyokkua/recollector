package ua.kostenko.recollector.app.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.dto.auth.*;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.*;
import ua.kostenko.recollector.app.repository.UserRepository;
import ua.kostenko.recollector.app.util.UserUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * AuthService handles user authentication, registration, login, and password management,
 * including password reset and forgot password functionality. It integrates with Spring Security
 * by implementing the AuthenticationManager interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthenticationManager {

    private final UserUtils userUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Authenticates the user using the provided email and password.
     *
     * @param authentication Contains the user's email and password for authentication.
     *
     * @return An Authentication object if authentication is successful.
     *
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = String.valueOf(authentication.getPrincipal());
        String password = String.valueOf(authentication.getCredentials());
        log.info("Authenticating user with email: {}", email);

        UserDto userDto = loginUser(email, password);

        log.info("User '{}' authenticated successfully", email);
        return new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getJwtToken(), List.of());
    }

    /**
     * Registers a new user based on the provided registration data.
     *
     * @param requestDto The registration request containing the user's email and password.
     *
     * @return A UserDto containing the registered user's email.
     *
     * @throws UserRegistrationException if the user already exists or if registration fails.
     */
    public UserDto registerUser(RegisterRequestDto requestDto) {
        log.info("Registering user with email: {}", requestDto.getEmail());
        userUtils.validateRegisterRequestDto(requestDto);

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            log.warn("User registration failed: User with email '{}' already exists", requestDto.getEmail());
            throw new UserRegistrationException("User with email '" + requestDto.getEmail() + "' already exists");
        }

        User newUser = createNewUser(requestDto);
        return saveUserAndReturnDto(newUser, "registered");
    }

    /**
     * Finds a user by email. Validates the email format before searching.
     *
     * @param email The email of the user to find.
     *
     * @return The User entity if found.
     *
     * @throws UserCredentialsValidationException if the email format is invalid.
     * @throws UserNotFoundException              if no user is found with the provided email.
     */
    public User findUserByEmail(String email) {
        validateEmail(email);

        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Search of user by email failed: User with email '{}' not found", email);
            return new UserNotFoundException("User with email '" + email + "' not found");
        });
    }

    /**
     * Logs in a user by verifying the provided email and password.
     *
     * @param email    The user's email.
     * @param password The user's password.
     *
     * @return A UserDto containing the user's email and a JWT token if login is successful.
     *
     * @throws UserCredentialsValidationException if the email or password format is invalid.
     * @throws UserLoginException                 if the password is incorrect.
     */
    public UserDto loginUser(String email, String password) {
        log.info("Attempting to log in user with email: {}", email);

        validateEmailAndPassword(email, password);

        User user = findUserByEmail(email);
        verifyPassword(password, user);

        String jwtToken = jwtUtil.generateToken(email);
        log.debug("Generated JWT token for user with email '{}'", email);

        updateLastLogin(user);

        log.info("User with email '{}' logged in successfully", email);
        return UserDto.builder().email(email).jwtToken(jwtToken).build();
    }

    /**
     * Processes a forgot password request and generates a reset token.
     *
     * @param requestDto The forgot password request containing the user's email.
     *
     * @throws UserForgotPasswordTooManyRequestsException if the user has already requested a reset token recently.
     */
    public void forgotPassword(ForgotPasswordRequestDto requestDto) {
        log.info("Processing forgot password request for email: {}", requestDto.getEmail());

        User user = findUserByEmail(requestDto.getEmail());
        checkResetTokenAvailability(user);
        generateAndSaveResetToken(user);

        log.info("Forgot password request processed successfully for email: {}. Reset token generated.",
                 requestDto.getEmail());
        log.debug("Generated reset token for email '{}': {}", requestDto.getEmail(), user.getResetToken());
    }

    /**
     * Resets the user's password using the provided reset token and new password.
     *
     * @param resetRequest The reset password request containing the user's email, reset token, and new password.
     *
     * @return A UserDto containing the user's email.
     *
     * @throws UserResetPasswordRequiredValuesException if the reset token is empty or does not match.
     */
    public UserDto resetPassword(ResetPasswordRequestDto resetRequest) {
        log.info("Resetting password for user with email: {}", resetRequest.getEmail());
        userUtils.validateResetPasswordRequestDto(resetRequest);

        validateResetToken(resetRequest);

        User user = findUserByEmail(resetRequest.getEmail());
        validatePasswordAlreadyInUse(resetRequest.getPassword(), user);

        user.setPasswordHash(passwordEncoder.encode(resetRequest.getPassword()));
        clearResetToken(user);

        return saveUserAndReturnDto(user, "password reset");
    }

    /**
     * Changes the user's password based on the provided request data.
     *
     * @param requestDto The change password request containing the user's email, current password, and new password.
     *
     * @return A UserDto containing the user's email.
     *
     * @throws UserChangePasswordException if the current password is incorrect or the new password is already in use.
     */
    public UserDto changePassword(ChangePasswordRequestDto requestDto) {
        userUtils.validateChangePasswordRequestDto(requestDto);

        User user = findUserByEmail(requestDto.getEmail());
        verifyPassword(requestDto.getPasswordCurrent(), user);

        validatePasswordAlreadyInUse(requestDto.getPassword(), user);

        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        clearResetToken(user);

        return saveUserAndReturnDto(user, "password changed");
    }

    public String deleteAccount(AccountDeleteRequestDto requestDto) {
        userUtils.validateAccountDeleteRequestDto(requestDto);

        User user = findUserByEmail(requestDto.getEmail());
        verifyPassword(requestDto.getPassword(), user);

        userRepository.delete(user);

        return "Successfully deleted account '" + requestDto.getEmail() + "'";
    }

    /**
     * Retrieves the currently authenticated user's email from the security context.
     *
     * @return The email of the authenticated user.
     *
     * @throws UserNotAuthenticatedException if the user is not authenticated.
     */
    @NonNull
    public String getUserEmailFromAuthContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        return authentication.getName();
    }

    /**
     * Validates if the new password is already in use by the user.
     *
     * @param newPassword The new password to check.
     * @param user        The user whose password is being changed.
     *
     * @throws UserChangePasswordException if the new password matches the existing password.
     */
    public void validatePasswordAlreadyInUse(String newPassword, User user) {
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new UserChangePasswordException("Password already in use. Create a brand new password");
        }
    }

    private void validateEmail(String email) {
        if (!userUtils.isEmailValid(email)) {
            log.warn("Search of user by email failed: Invalid email format for email: {}", email);
            throw new UserCredentialsValidationException("Email or Password has wrong format");
        }
    }

    private void validateEmailAndPassword(String email, String password) {
        validateEmail(email);
        if (!userUtils.isPasswordValid(password)) {
            log.warn("Login failed: Invalid password format for email: {}", email);
            throw new UserCredentialsValidationException("Email or Password has wrong format");
        }
    }

    private void verifyPassword(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Login failed: Wrong password for user with email '{}'", user.getEmail());
            throw new UserLoginException("Wrong password");
        }
    }

    private void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.saveAndFlush(user);
    }

    private void checkResetTokenAvailability(User user) {
        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(user.getResetToken()) && Objects.nonNull(user.getResetTokenExpiry()) && user.getResetTokenExpiry()
                                                                                                        .isAfter(now)) {
            long timeToWait = ChronoUnit.MINUTES.between(now, user.getResetTokenExpiry());
            log.warn("Forgot password failed: Too many requests for password restore. Time to wait: {} minutes",
                     timeToWait);
            throw new UserForgotPasswordTooManyRequestsException(
                    "Too many requests for password restore. You need to wait: " + timeToWait + " minutes");
        }
    }

    private void generateAndSaveResetToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.saveAndFlush(user);
    }

    private void validateResetToken(ResetPasswordRequestDto resetRequest) {
        if (StringUtils.isBlank(resetRequest.getPasswordResetToken())) {
            log.warn("Password reset failed: Reset token is empty for email: {}", resetRequest.getEmail());
            throw new UserResetPasswordRequiredValuesException("Password reset token is empty");
        }
        User user = findUserByEmail(resetRequest.getEmail());
        if (!resetRequest.getPasswordResetToken().equals(user.getResetToken())) {
            log.warn("Password reset failed: Reset token does not match for email: {}", resetRequest.getEmail());
            throw new UserResetPasswordRequiredValuesException("Password reset token does not match");
        }
    }

    private void clearResetToken(User user) {
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.saveAndFlush(user);
    }

    private User createNewUser(RegisterRequestDto requestDto) {
        LocalDateTime creationTime = LocalDateTime.now();
        return User.builder()
                   .email(requestDto.getEmail())
                   .passwordHash(passwordEncoder.encode(requestDto.getPassword()))
                   .updatedAt(creationTime)
                   .createdAt(creationTime)
                   .lastLogin(creationTime)
                   .resetToken(null)
                   .resetTokenExpiry(null)
                   .build();
    }

    private UserDto saveUserAndReturnDto(User user, String action) {
        try {
            User savedUser = userRepository.save(user);
            log.info("User with email '{}' {} successfully", savedUser.getEmail(), action);
            return UserDto.builder().email(savedUser.getEmail()).build();
        } catch (Exception ex) {
            log.error("User {} failed for email '{}': {}", action, user.getEmail(), ex.getMessage(), ex);
            throw new UserRegistrationException(ex.getMessage());
        }
    }
}
