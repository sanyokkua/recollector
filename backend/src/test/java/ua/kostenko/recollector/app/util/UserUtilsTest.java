package ua.kostenko.recollector.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ua.kostenko.recollector.app.dto.auth.AccountDeleteRequestDto;
import ua.kostenko.recollector.app.dto.auth.ChangePasswordRequestDto;
import ua.kostenko.recollector.app.dto.auth.RegisterRequestDto;
import ua.kostenko.recollector.app.dto.auth.ResetPasswordRequestDto;
import ua.kostenko.recollector.app.exception.UserAccountDeleteException;
import ua.kostenko.recollector.app.exception.UserChangePasswordException;
import ua.kostenko.recollector.app.exception.UserRegistrationException;
import ua.kostenko.recollector.app.exception.UserResetPasswordRequiredValuesException;

import static org.junit.jupiter.api.Assertions.*;

class UserUtilsTest {

    private UserUtils userUtils;

    @BeforeEach
    void setUp() {
        userUtils = new UserUtils();
    }

    @Test
    void isEmailValid_givenValidEmail_returnsTrue() {
        // Arrange
        String email = "test@example.com";

        // Act
        boolean isValid = userUtils.isEmailValid(email);

        // Assert
        assertTrue(isValid, "Email should be valid");
    }

    @Test
    void isEmailValid_givenInvalidEmail_returnsFalse() {
        // Arrange
        String email = "invalid-email";

        // Act
        boolean isValid = userUtils.isEmailValid(email);

        // Assert
        assertFalse(isValid, "Email should be invalid");
    }

    @Test
    void isEmailValid_givenBlankEmail_returnsFalse() {
        // Arrange
        String email = "";

        // Act
        boolean isValid = userUtils.isEmailValid(email);

        // Assert
        assertFalse(isValid, "Email should be invalid");
    }

    @Test
    void isPasswordValid_givenValidPassword_returnsTrue() {
        // Arrange
        String password = "ValidPass123";

        // Act
        boolean isValid = userUtils.isPasswordValid(password);

        // Assert
        assertTrue(isValid, "Password should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {"short", "", "ThisPasswordIsWayTooLong123"})
    void isPasswordValid_givenVariousInvalidPasswords_returnsFalse(String password) {
        // Act
        boolean isValid = userUtils.isPasswordValid(password);

        // Assert
        assertFalse(isValid, "Password should be invalid for input: " + password);
    }

    @Test
    void validateRegisterRequestDto_givenNullDto_throwsException() {
        // Act & Assert
        UserRegistrationException thrown = assertThrows(UserRegistrationException.class,
                                                        () -> userUtils.validateRegisterRequestDto(null));
        assertEquals("Request DTO is null", thrown.getMessage(), "Exception message should match");
    }

    @Test
    void validateRegisterRequestDto_givenValidDto_doesNotThrowException() {
        // Arrange
        RegisterRequestDto dto = RegisterRequestDto.builder()
                                                   .email("valid@email.com")
                                                   .password("NotShortPassword")
                                                   .passwordConfirm("NotShortPassword")
                                                   .build();

        // Act & Assert
        assertDoesNotThrow(() -> userUtils.validateRegisterRequestDto(dto));
    }

    @Test
    void validateRegisterRequestDto_givenInvalidDto_throwsException() {
        // Arrange
        RegisterRequestDto dto = RegisterRequestDto.builder()
                                                   .email("invalid-email")
                                                   .password("short")
                                                   .passwordConfirm("shot")
                                                   .build();

        // Act & Assert
        UserRegistrationException thrown = assertThrows(UserRegistrationException.class,
                                                        () -> userUtils.validateRegisterRequestDto(dto));
        assertTrue(thrown.getMessage().contains("Email is not valid"),
                   "Error message should contain 'Email is not valid'");
        assertTrue(thrown.getMessage().contains("Password is not valid"),
                   "Error message should contain 'Password is not valid'");
        assertTrue(thrown.getMessage().contains("Passwords do not match"),
                   "Error message should contain 'Passwords do not match'");
    }

    @Test
    void validateResetPasswordRequestDto_givenValidDto_doesNotThrowException() {
        // Arrange
        ResetPasswordRequestDto dto = ResetPasswordRequestDto.builder()
                                                             .email("valid@email.com")
                                                             .password("NotShortPassword")
                                                             .passwordConfirm("NotShortPassword")
                                                             .passwordResetToken("y2udvgfty32tdfyveywdvytewvytdvtywe")
                                                             .build();

        // Act & Assert
        assertDoesNotThrow(() -> userUtils.validateResetPasswordRequestDto(dto));
    }

    @Test
    void validateResetPasswordRequestDto_givenInvalidDto_throwsException() {
        // Arrange
        ResetPasswordRequestDto dto = ResetPasswordRequestDto.builder()
                                                             .email("invalid-email")
                                                             .password("short")
                                                             .passwordConfirm("shot")
                                                             .passwordResetToken("")
                                                             .build();

        // Act & Assert
        UserResetPasswordRequiredValuesException thrown = assertThrows(UserResetPasswordRequiredValuesException.class,
                                                                       () -> userUtils.validateResetPasswordRequestDto(
                                                                               dto));
        assertTrue(thrown.getMessage().contains("Email is not valid"),
                   "Error message should contain 'Email is not valid'");
        assertTrue(thrown.getMessage().contains("Password is not valid"),
                   "Error message should contain 'Password is not valid'");
        assertTrue(thrown.getMessage().contains("Password Confirm is not valid"),
                   "Error message should contain 'Password Confirm is not valid'");
        assertTrue(thrown.getMessage().contains("Passwords do not match"),
                   "Error message should contain 'Passwords do not match'");
        assertTrue(thrown.getMessage().contains("Reset token can't be blank"),
                   "Error message should contain 'Reset token can't be blank'");
    }

    @Test
    void validateChangePasswordRequestDto_givenValidDto_doesNotThrowException() {
        // Arrange
        ChangePasswordRequestDto dto = ChangePasswordRequestDto.builder()
                                                               .email("valid@email.com")
                                                               .password("NotShortPassword")
                                                               .passwordConfirm("NotShortPassword")
                                                               .passwordCurrent("CurrentPassword")
                                                               .build();

        // Act & Assert
        assertDoesNotThrow(() -> userUtils.validateChangePasswordRequestDto(dto));
    }

    @Test
    void validateChangePasswordRequestDto_givenInvalidDto_throwsException() {
        // Arrange
        ChangePasswordRequestDto dto = ChangePasswordRequestDto.builder()
                                                               .email("invalid-email")
                                                               .password("short")
                                                               .passwordConfirm("shot")
                                                               .passwordCurrent("short")
                                                               .build();

        // Act & Assert
        UserChangePasswordException thrown = assertThrows(UserChangePasswordException.class,
                                                          () -> userUtils.validateChangePasswordRequestDto(dto));
        assertTrue(thrown.getMessage().contains("Email is not valid"),
                   "Error message should contain 'Email is not valid'");
        assertTrue(thrown.getMessage().contains("Password is not valid"),
                   "Error message should contain 'Password is not valid'");
        assertTrue(thrown.getMessage().contains("Passwords do not match"),
                   "Error message should contain 'Passwords do not match'");
        assertTrue(thrown.getMessage().contains("Current Password is not valid (in terms of structure and length)"),
                   "Error message should contain 'Current Password is not valid (in terms of structure and length)'");
        assertTrue(thrown.getMessage().contains("New and Current Passwords should not match"),
                   "Error message should contain 'New and Current Passwords should not match'");
    }

    @Test
    void validateAccountDeleteRequestDto_givenValidDto_doesNotThrowException() {
        // Arrange
        AccountDeleteRequestDto dto = AccountDeleteRequestDto.builder()
                                                             .email("valid@email.com")
                                                             .password("NotShortPassword")
                                                             .passwordConfirm("NotShortPassword")
                                                             .build();

        // Act & Assert
        assertDoesNotThrow(() -> userUtils.validateAccountDeleteRequestDto(dto));
    }

    @Test
    void validateAccountDeleteRequestDto_givenInvalidDto_throwsException() {
        // Arrange
        AccountDeleteRequestDto dto = AccountDeleteRequestDto.builder()
                                                             .email("invalid-email")
                                                             .password("short")
                                                             .passwordConfirm("shot")
                                                             .build();

        // Act & Assert
        UserAccountDeleteException thrown = assertThrows(UserAccountDeleteException.class,
                                                         () -> userUtils.validateAccountDeleteRequestDto(dto));
        assertTrue(thrown.getMessage().contains("Email is not valid"),
                   "Error message should contain 'Email is not valid'");
        assertTrue(thrown.getMessage().contains("Password is not valid"),
                   "Error message should contain 'Password is not valid'");
        assertTrue(thrown.getMessage().contains("Passwords do not match"),
                   "Error message should contain 'Passwords do not match'");
    }
}
