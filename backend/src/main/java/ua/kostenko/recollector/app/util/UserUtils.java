package ua.kostenko.recollector.app.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ua.kostenko.recollector.app.dto.auth.AccountDeleteRequestDto;
import ua.kostenko.recollector.app.dto.auth.ChangePasswordRequestDto;
import ua.kostenko.recollector.app.dto.auth.RegisterRequestDto;
import ua.kostenko.recollector.app.dto.auth.ResetPasswordRequestDto;
import ua.kostenko.recollector.app.exception.UserAccountDeleteException;
import ua.kostenko.recollector.app.exception.UserChangePasswordException;
import ua.kostenko.recollector.app.exception.UserRegistrationException;
import ua.kostenko.recollector.app.exception.UserResetPasswordRequiredValuesException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling user-related validation and utility functions.
 * It provides methods for validating email, password, and various request DTOs.
 */
@Slf4j
@Component
public class UserUtils {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 16;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Validates the email format.
     *
     * @param email The email to validate.
     *
     * @return true if the email format is valid, false otherwise.
     */
    public boolean isEmailValid(String email) {
        if (StringUtils.isBlank(email)) {
            log.debug("Email validation failed: Email is blank");
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        boolean isValid = matcher.matches();
        if (!isValid) {
            log.debug("Email validation failed: Invalid email format for email '{}'", email);
        }
        return isValid;
    }

    /**
     * Validates the password format.
     *
     * @param password The password to validate.
     *
     * @return true if the password length is within the valid range, false otherwise.
     */
    public boolean isPasswordValid(String password) {
        if (StringUtils.isBlank(password)) {
            log.debug("Password validation failed: Password is blank");
            return false;
        }
        boolean isValid = password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH;
        if (!isValid) {
            log.debug("Password validation failed: Password length is out of bounds for password '{}'", password);
        }
        return isValid;
    }

    /**
     * Validates the registration request DTO.
     *
     * @param requestDto The registration request DTO.
     *
     * @throws UserRegistrationException if any validation fails.
     */
    public void validateRegisterRequestDto(RegisterRequestDto requestDto) {
        validateRequestDtoNotNull(requestDto);

        var email = requestDto.getEmail();
        var password = requestDto.getPassword();
        var passwordConfirm = requestDto.getPasswordConfirm();

        var errors = new ArrayList<String>();
        validateEmail(email, errors);
        validatePasswordAndConfirmation(password, passwordConfirm, errors);

        if (!errors.isEmpty()) {
            log.warn("User registration failed: {}", String.join(", ", errors));
            throw new UserRegistrationException(errors);
        }
    }

    /**
     * Validates the reset password request DTO.
     *
     * @param requestDto The reset password request DTO.
     *
     * @throws UserResetPasswordRequiredValuesException if any validation fails.
     */
    public void validateResetPasswordRequestDto(ResetPasswordRequestDto requestDto) {
        validateRequestDtoNotNull(requestDto);

        var email = requestDto.getEmail();
        var password = requestDto.getPassword();
        var passwordConfirm = requestDto.getPasswordConfirm();
        var passwordResetToken = requestDto.getPasswordResetToken();

        var errors = new ArrayList<String>();
        validateEmail(email, errors);
        validatePasswordAndConfirmation(password, passwordConfirm, errors);
        validateResetToken(passwordResetToken, errors);

        if (!errors.isEmpty()) {
            log.warn("Password reset failed: {}", String.join(", ", errors));
            throw new UserResetPasswordRequiredValuesException(errors);
        }
    }

    /**
     * Validates the change password request DTO.
     *
     * @param requestDto The change password request DTO.
     *
     * @throws UserChangePasswordException if any validation fails.
     */
    public void validateChangePasswordRequestDto(ChangePasswordRequestDto requestDto) {
        validateRequestDtoNotNull(requestDto);

        var email = requestDto.getEmail();
        var password = requestDto.getPassword();
        var passwordConfirm = requestDto.getPasswordConfirm();
        var passwordCurrent = requestDto.getPasswordCurrent();

        var errors = new ArrayList<String>();
        validateEmail(email, errors);
        validatePasswordAndConfirmation(password, passwordConfirm, errors);
        validateCurrentPassword(passwordCurrent, password, errors);

        if (!errors.isEmpty()) {
            log.warn("Password change failed: {}", String.join(", ", errors));
            throw new UserChangePasswordException(errors);
        }
    }

    public void validateAccountDeleteRequestDto(AccountDeleteRequestDto requestDto) {
        validateRequestDtoNotNull(requestDto);

        var email = requestDto.getEmail();
        var password = requestDto.getPassword();
        var passwordConfirm = requestDto.getPasswordConfirm();

        var errors = new ArrayList<String>();
        validateEmail(email, errors);
        validatePasswordAndConfirmation(password, passwordConfirm, errors);

        if (!errors.isEmpty()) {
            log.warn("Account Delete change failed: {}", String.join(", ", errors));
            throw new UserAccountDeleteException(errors);
        }
    }

    /**
     * Validates if the provided DTO is null.
     *
     * @param requestDto The request DTO to validate.
     *
     * @throws UserRegistrationException if the DTO is null.
     */
    private void validateRequestDtoNotNull(Object requestDto) {
        if (Objects.isNull(requestDto)) {
            log.warn("Validation failed: Request DTO is null");
            throw new UserRegistrationException("Request DTO is null");
        }
    }

    /**
     * Validates the email and adds any errors to the provided list.
     *
     * @param email  The email to validate.
     * @param errors The list to add any validation errors to.
     */
    private void validateEmail(String email, List<String> errors) {
        if (!isEmailValid(email)) {
            errors.add("Email is not valid");
        }
    }

    /**
     * Validates the password, confirmation, and their matching status, adding any errors to the provided list.
     *
     * @param password        The password to validate.
     * @param passwordConfirm The password confirmation to validate.
     * @param errors          The list to add any validation errors to.
     */
    private void validatePasswordAndConfirmation(String password, String passwordConfirm, List<String> errors) {
        boolean isPasswordValid = isPasswordValid(password);
        boolean isPasswordConfirmValid = isPasswordValid(passwordConfirm);

        if (!isPasswordValid) {
            errors.add("Password is not valid");
        }
        if (!isPasswordConfirmValid) {
            errors.add("Password Confirm is not valid");
        }
        if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(passwordConfirm) && !password.equals(
                passwordConfirm)) {
            errors.add("Passwords do not match");
        }
    }

    /**
     * Validates the reset token and adds any errors to the provided list.
     *
     * @param passwordResetToken The reset token to validate.
     * @param errors             The list to add any validation errors to.
     */
    private void validateResetToken(String passwordResetToken, List<String> errors) {
        if (StringUtils.isBlank(passwordResetToken)) {
            errors.add("Reset token can't be blank");
        }
    }

    /**
     * Validates the current password and its difference from the new password, adding any errors to the provided list.
     *
     * @param currentPassword The current password.
     * @param newPassword     The new password.
     * @param errors          The list to add any validation errors to.
     */
    private void validateCurrentPassword(String currentPassword, String newPassword, List<String> errors) {
        boolean isCurrentPasswordValid = isPasswordValid(currentPassword);

        if (!isCurrentPasswordValid) {
            errors.add("Current Password is not valid (in terms of structure and length)");
        }
        if (StringUtils.isNotBlank(currentPassword) && StringUtils.isNotBlank(newPassword) && currentPassword.equals(
                newPassword)) {
            errors.add("New and Current Passwords should not match");
        }
    }
}
