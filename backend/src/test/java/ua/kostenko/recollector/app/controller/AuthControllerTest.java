package ua.kostenko.recollector.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.dto.auth.*;
import ua.kostenko.recollector.app.exception.*;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.security.JwtUtil;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private static final String BASE_URL = "/api/v1/auth";
    private static final String VALID_EMAIL = "valid@email.com";
    private static final String PASSWORD = "testPassword";
    private static final String BAD_REQUEST_MESSAGE = "Bad request";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    // Test Scenarios
    private static Stream<Arguments> registerExceptionScenarios() {
        return Stream.of(Arguments.of(new UserRegistrationException("Failed to register user"),
                                      HttpStatus.BAD_REQUEST,
                                      "Failed to register user"),
                         Arguments.of(new UserCredentialsValidationException("Failed to register user"),
                                      HttpStatus.BAD_REQUEST,
                                      "Failed to register user"));
    }

    private static Stream<Arguments> loginExceptionScenarios() {
        return Stream.of(Arguments.of(new UserCredentialsValidationException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserNotFoundException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.NOT_FOUND,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserLoginException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.UNAUTHORIZED,
                                      BAD_REQUEST_MESSAGE));
    }

    private static Stream<Arguments> resetExceptionScenarios() {
        return Stream.of(Arguments.of(new UserCredentialsValidationException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserRegistrationException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserResetPasswordRequiredValuesException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserChangePasswordException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserNotFoundException("Not Found"), HttpStatus.NOT_FOUND, "Not Found"));
    }

    public static Stream<Arguments> changePasswordExceptionScenarios() {
        return Stream.of(Arguments.of(new UserChangePasswordException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserRegistrationException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserLoginException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.UNAUTHORIZED,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserNotFoundException("Not Found"), HttpStatus.NOT_FOUND, "Not Found"));
    }

    public static Stream<Arguments> deleteAccountExceptionScenarios() {
        return Stream.of(Arguments.of(new UserAccountDeleteException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserRegistrationException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.BAD_REQUEST,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserLoginException(BAD_REQUEST_MESSAGE),
                                      HttpStatus.UNAUTHORIZED,
                                      BAD_REQUEST_MESSAGE),
                         Arguments.of(new UserNotFoundException("Not Found"), HttpStatus.NOT_FOUND, "Not Found"));
    }

    // Test Methods
    @Test
    @DisplayName("Register User - Valid Request")
    void registerUser_validRequest_shouldReturnUserDto() throws Exception {
        RegisterRequestDto requestDto = createRegisterRequest();
        UserDto userDto = UserDto.builder().email(requestDto.getEmail()).build();

        when(authService.registerUser(requestDto)).thenReturn(userDto);

        performPostRequest("/register", requestDto).andExpect(status().isCreated())
                                                   .andExpect(jsonPath("$.statusCode").value(201))
                                                   .andExpect(jsonPath("$.statusMessage").value("CREATED"))
                                                   .andExpect(jsonPath("$.data.email").value(requestDto.getEmail()))
                                                   .andExpect(jsonPath("$.data.jwtToken").doesNotExist())
                                                   .andExpect(jsonPath("$.meta").doesNotExist())
                                                   .andExpect(jsonPath("$.error").doesNotExist());
    }

    @ParameterizedTest
    @MethodSource("registerExceptionScenarios")
    @DisplayName("Register User - Invalid Request")
    void registerUser_invalidRequest_shouldReturnException(Exception exception, HttpStatus status,
                                                           String errorMessage) throws Exception {
        RegisterRequestDto requestDto = createRegisterRequest();

        when(authService.registerUser(requestDto)).thenThrow(exception);

        performPostRequest("/register", requestDto).andExpect(status().is(status.value()))
                                                   .andExpect(jsonPath("$.statusCode").value(status.value()))
                                                   .andExpect(jsonPath("$.statusMessage").value(status.name()))
                                                   .andExpect(jsonPath("$.data").doesNotExist())
                                                   .andExpect(jsonPath("$.error").value(exception.getClass()
                                                                                                 .getSimpleName() + ": " + errorMessage));
    }

    @Test
    @DisplayName("Login User - Valid Request")
    void loginUser_validRequest_shouldReturnUserDto() throws Exception {
        LoginRequestDto requestDto = createLoginRequest();
        var authentication = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        when(authService.authenticate(any(Authentication.class))).thenReturn(authentication);

        performPostRequest("/login", requestDto).andExpect(status().isOk())
                                                .andExpect(jsonPath("$.statusCode").value(200))
                                                .andExpect(jsonPath("$.statusMessage").value("OK"))
                                                .andExpect(jsonPath("$.data.email").value(requestDto.getEmail()))
                                                .andExpect(jsonPath("$.data.jwtToken").value(requestDto.getPassword()))
                                                .andExpect(jsonPath("$.meta").doesNotExist())
                                                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @ParameterizedTest
    @MethodSource("loginExceptionScenarios")
    @DisplayName("Login User - Invalid Request")
    void loginUser_invalidRequest_shouldReturnException(Exception exception, HttpStatus status,
                                                        String errorMessage) throws Exception {
        LoginRequestDto requestDto = createLoginRequest();

        when(authService.authenticate(any(Authentication.class))).thenThrow(exception);

        performPostRequest("/login", requestDto).andExpect(status().is(status.value()))
                                                .andExpect(jsonPath("$.statusCode").value(status.value()))
                                                .andExpect(jsonPath("$.statusMessage").value(status.name()))
                                                .andExpect(jsonPath("$.data").doesNotExist())
                                                .andExpect(jsonPath("$.error").value(exception.getClass()
                                                                                              .getSimpleName() + ": " + errorMessage));
    }

    @Test
    @DisplayName("Forgot Password - Valid Request")
    void forgotPassword_validRequest_shouldReturnString() throws Exception {
        ForgotPasswordRequestDto requestDto = createForgotPasswordRequest();

        performPostRequest("/forgot-password", requestDto).andExpect(status().isOk())
                                                          .andExpect(jsonPath("$.statusCode").value(200))
                                                          .andExpect(jsonPath("$.statusMessage").value("OK"))
                                                          .andExpect(jsonPath("$.data").value("Password reset link sent"))
                                                          .andExpect(jsonPath("$.meta").doesNotExist())
                                                          .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    @DisplayName("Reset Password - Valid Request")
    void resetPassword_validRequest_shouldReturnUserDto() throws Exception {
        ResetPasswordRequestDto requestDto = createResetPasswordRequest();
        UserDto userDto = UserDto.builder().email(requestDto.getEmail()).build();

        when(authService.resetPassword(requestDto)).thenReturn(userDto);

        performPostRequest("/reset-password", requestDto).andExpect(status().isOk())
                                                         .andExpect(jsonPath("$.statusCode").value(200))
                                                         .andExpect(jsonPath("$.statusMessage").value("OK"))
                                                         .andExpect(jsonPath("$.data.email").value(requestDto.getEmail()))
                                                         .andExpect(jsonPath("$.data.jwtToken").doesNotExist())
                                                         .andExpect(jsonPath("$.meta").doesNotExist())
                                                         .andExpect(jsonPath("$.error").doesNotExist());
    }

    @ParameterizedTest
    @MethodSource("resetExceptionScenarios")
    @DisplayName("Reset Password - Invalid Request")
    void resetPassword_invalidRequest_shouldReturnException(Exception exception, HttpStatus status,
                                                            String errorMessage) throws Exception {
        ResetPasswordRequestDto requestDto = createResetPasswordRequest();

        when(authService.resetPassword(any(ResetPasswordRequestDto.class))).thenThrow(exception);

        performPostRequest("/reset-password", requestDto).andExpect(status().is(status.value()))
                                                         .andExpect(jsonPath("$.statusCode").value(status.value()))
                                                         .andExpect(jsonPath("$.statusMessage").value(status.name()))
                                                         .andExpect(jsonPath("$.data").doesNotExist())
                                                         .andExpect(jsonPath("$.error").value(exception.getClass()
                                                                                                       .getSimpleName() + ": " + errorMessage));
    }

    @Test
    @DisplayName("Change Password - Valid Request")
    void changePassword_validRequest_shouldReturnUserDto() throws Exception {
        ChangePasswordRequestDto requestDto = createChangePasswordRequest();
        UserDto userDto = UserDto.builder().email(requestDto.getEmail()).build();

        when(authService.changePassword(requestDto)).thenReturn(userDto);

        performPostRequest("/change-password", requestDto).andExpect(status().isOk())
                                                          .andExpect(jsonPath("$.statusCode").value(200))
                                                          .andExpect(jsonPath("$.statusMessage").value("OK"))
                                                          .andExpect(jsonPath("$.data.email").value(requestDto.getEmail()))
                                                          .andExpect(jsonPath("$.data.jwtToken").doesNotExist())
                                                          .andExpect(jsonPath("$.meta").doesNotExist())
                                                          .andExpect(jsonPath("$.error").doesNotExist());
    }

    @ParameterizedTest
    @MethodSource("changePasswordExceptionScenarios")
    @DisplayName("Change Password - Invalid Request")
    void changePassword_invalidRequest_shouldReturnException(Exception exception, HttpStatus status,
                                                             String errorMessage) throws Exception {
        ChangePasswordRequestDto requestDto = createChangePasswordRequest();

        when(authService.changePassword(any(ChangePasswordRequestDto.class))).thenThrow(exception);

        performPostRequest("/change-password", requestDto).andExpect(status().is(status.value()))
                                                          .andExpect(jsonPath("$.statusCode").value(status.value()))
                                                          .andExpect(jsonPath("$.statusMessage").value(status.name()))
                                                          .andExpect(jsonPath("$.data").doesNotExist())
                                                          .andExpect(jsonPath("$.error").value(exception.getClass()
                                                                                                        .getSimpleName() + ": " + errorMessage));
    }

    @Test
    @DisplayName("Delete Account - Valid Request")
    void deleteAccount_validRequest_shouldReturnString() throws Exception {
        AccountDeleteRequestDto requestDto = createAccountDeleteRequest();
        when(authService.deleteAccount(requestDto)).thenReturn("Successfully deleted account");

        performPostRequest("/delete-account", requestDto).andExpect(status().isOk())
                                                         .andExpect(jsonPath("$.statusCode").value(200))
                                                         .andExpect(jsonPath("$.statusMessage").value("OK"))
                                                         .andExpect(jsonPath("$.data").value(
                                                                 "Successfully deleted account"))
                                                         .andExpect(jsonPath("$.meta").doesNotExist())
                                                         .andExpect(jsonPath("$.error").doesNotExist());
    }

    @ParameterizedTest
    @MethodSource("deleteAccountExceptionScenarios")
    @DisplayName("Delete Account - Invalid Request")
    void deleteAccount_invalidRequest_shouldReturnException(Exception exception, HttpStatus status,
                                                            String errorMessage) throws Exception {
        AccountDeleteRequestDto requestDto = createAccountDeleteRequest();
        when(authService.deleteAccount(requestDto)).thenThrow(exception);

        performPostRequest("/delete-account", requestDto).andExpect(status().is(status.value()))
                                                         .andExpect(jsonPath("$.statusCode").value(status.value()))
                                                         .andExpect(jsonPath("$.statusMessage").value(status.name()))
                                                         .andExpect(jsonPath("$.data").doesNotExist())
                                                         .andExpect(jsonPath("$.error").value(exception.getClass()
                                                                                                       .getSimpleName() + ": " + errorMessage));
    }

    // Helper Methods
    private ResultActions performPostRequest(String endpoint, Object requestDto) throws Exception {
        return mockMvc.perform(post(BASE_URL + endpoint).contentType(MediaType.APPLICATION_JSON)
                                                        .content(objectMapper.writeValueAsString(requestDto)));
    }

    private RegisterRequestDto createRegisterRequest() {
        return RegisterRequestDto.builder().email(VALID_EMAIL).password(PASSWORD).build();
    }

    private LoginRequestDto createLoginRequest() {
        return LoginRequestDto.builder().email(VALID_EMAIL).password(PASSWORD).build();
    }

    private ForgotPasswordRequestDto createForgotPasswordRequest() {
        return ForgotPasswordRequestDto.builder().email(VALID_EMAIL).build();
    }

    private ResetPasswordRequestDto createResetPasswordRequest() {
        return ResetPasswordRequestDto.builder().email(VALID_EMAIL).password(PASSWORD).build();
    }

    private ChangePasswordRequestDto createChangePasswordRequest() {
        return ChangePasswordRequestDto.builder().email(VALID_EMAIL).password(PASSWORD).build();
    }

    private AccountDeleteRequestDto createAccountDeleteRequest() {
        return AccountDeleteRequestDto.builder()
                                      .email(VALID_EMAIL)
                                      .password(PASSWORD)
                                      .passwordConfirm(PASSWORD)
                                      .build();
    }
}
