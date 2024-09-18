package ua.kostenko.recollector.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.dto.auth.*;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.UserLoginException;
import ua.kostenko.recollector.app.exception.UserNotAuthenticatedException;
import ua.kostenko.recollector.app.exception.UserNotFoundException;
import ua.kostenko.recollector.app.exception.UserRegistrationException;
import ua.kostenko.recollector.app.repository.InvalidatedTokenRepository;
import ua.kostenko.recollector.app.repository.UserRepository;
import ua.kostenko.recollector.app.util.UserUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Spy
    private UserUtils userUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtHelperUtil jwtUtil;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private AuthenticationService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.reset(userUtils, passwordEncoder, userRepository, jwtUtil);
        authService = new AuthenticationService(userUtils,
                                                jwtUtil,
                                                passwordEncoder,
                                                userRepository,
                                                invalidatedTokenRepository);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpMinutes", 1);
        ReflectionTestUtils.setField(jwtUtil, "jwtRefreshExpHours", 1);
        ReflectionTestUtils.setField(authService, "jwtExpMinutes", 1);
        ReflectionTestUtils.setField(authService, "jwtRefreshExpHours", 1);
    }

    @Test
    void registerUser_validData_success() {
        User user = new User();
        user.setPasswordHash("encodedPassword");
        user.setEmail("test@example.com");

        RegisterRequestDto requestDto = new RegisterRequestDto("test@example.com", "validPassword", "validPassword");
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = authService.registerUser(requestDto);

        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void registerUser_duplicateEmail_throwsUserRegistrationException() {
        RegisterRequestDto requestDto = new RegisterRequestDto("test@example.com", "validPassword", "validPassword");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                                                           () -> authService.registerUser(requestDto));

        assertEquals("Email 'test@example.com' is already registered", exception.getMessage());
    }

    @Test
    void findUserByEmail_validEmail_userFound() {
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = authService.findUserByEmail(email);

        assertNotNull(result);
    }

    @Test
    void findUserByEmail_invalidEmail_throwsUserNotFoundException() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                                                       () -> authService.findUserByEmail(email));

        assertEquals("User with email 'nonexistent@example.com' not found", exception.getMessage());
    }

    @Test
    void loginUser_validCredentials_success() {
        String email = "test@example.com";
        String password = "validPassword";
        User user = new User();
        TokensDto tokensDto = TokensDto.builder()
                                       .refreshToken("jwtToken")
                                       .jwtToken("jwtToken")
                                       .jwtRefreshTokenExpirationDate(0)
                                       .jwtTokenExpirationDate(0)
                                       .build();
        user.setPasswordHash("encodedPassword");

        when(userUtils.isEmailValid(email)).thenReturn(true);
        when(userUtils.isPasswordValid(password)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateJwtTokensPair(email)).thenReturn(tokensDto);

        var result = authService.loginUser(email, password);

        assertEquals(email, result.getUserEmail());
        assertEquals("jwtToken", result.getTokensDto().getJwtToken());
    }

    @Test
    void loginUser_invalidPassword_throwsUserLoginException() {
        String email = "test@example.com";
        String password = "invalidPassword";
        User user = new User();
        user.setPasswordHash("encodedPassword");

        when(userUtils.isEmailValid(email)).thenReturn(true);
        when(userUtils.isPasswordValid(password)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPasswordHash())).thenReturn(false);

        UserLoginException exception = assertThrows(UserLoginException.class,
                                                    () -> authService.loginUser(email, password));

        assertEquals("Wrong password", exception.getMessage());
    }

    @Test
    void forgotPassword_validRequest_success() {
        ForgotPasswordRequestDto requestDto = new ForgotPasswordRequestDto("test@example.com");
        User user = new User();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        when(userUtils.isEmailValid(requestDto.getEmail())).thenReturn(true);

        authService.forgotPassword(requestDto);

        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void resetPassword_validRequest_success() {
        ResetPasswordRequestDto resetRequest = new ResetPasswordRequestDto("test@example.com",
                                                                           "validPassword",
                                                                           "validPassword",
                                                                           "resetToken");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");
        user.setResetToken("resetToken");

        when(userUtils.isEmailValid(resetRequest.getEmail())).thenReturn(true);
        when(userUtils.isPasswordValid(resetRequest.getPassword())).thenReturn(true);
        when(userUtils.isPasswordValid(resetRequest.getPasswordConfirm())).thenReturn(true);
        when(userRepository.findByEmail(resetRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(resetRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = authService.resetPassword(resetRequest);

        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void changePassword_validRequest_success() {
        ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto("test@example.com",
                                                                           "currentPassword",
                                                                           "newPassword",
                                                                           "newPassword");
        User user = new User();
        user.setEmail("test@example.com");
        user.setResetToken("resetToken");
        user.setPasswordHash("currentEncodedPassword");

        when(userUtils.isEmailValid(requestDto.getEmail())).thenReturn(true);
        when(userUtils.isPasswordValid(requestDto.getPassword())).thenReturn(true);
        when(userUtils.isPasswordValid(requestDto.getPasswordConfirm())).thenReturn(true);
        when(userUtils.isPasswordValid(requestDto.getPasswordCurrent())).thenReturn(true);
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(requestDto.getPasswordCurrent(), user.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = authService.changePassword(requestDto, "", "");

        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void deleteAccount_validRequest_success() {
        AccountDeleteRequestDto requestDto = new AccountDeleteRequestDto("test@example.com",
                                                                         "validPassword",
                                                                         "validPassword");
        User user = new User();
        user.setPasswordHash("encodedPassword");

        when(userUtils.isEmailValid(requestDto.getEmail())).thenReturn(true);
        when(userUtils.isPasswordValid(requestDto.getPassword())).thenReturn(true);
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(requestDto.getPassword(), user.getPasswordHash())).thenReturn(true);

        String result = authService.deleteAccount(requestDto);

        assertEquals("Successfully deleted account 'test@example.com'", result);
    }

    @Test
    void getUserEmailFromAuthContext_authenticatedUser_success() {
        String email = "test@example.com";
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(User.builder().email(email).build());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(User.builder().email(email).build()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var result = authService.getUserFromAuthContext();

        assertEquals(email, result.getEmail());
    }

    @Test
    void getUserEmailFromAuthContext_notAuthenticated_throwsUserNotAuthenticatedException() {
        SecurityContextHolder.getContext().setAuthentication(null);

        UserNotAuthenticatedException exception = assertThrows(UserNotAuthenticatedException.class,
                                                               () -> authService.getUserFromAuthContext());

        assertEquals("User is not authenticated", exception.getMessage());
    }
}