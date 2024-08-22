package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exceptions.*;
import ua.kostenko.recollector.app.repository.UserRepository;
import ua.kostenko.recollector.app.util.JwtUtil;
import ua.kostenko.recollector.app.util.UserValidationUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthenticationManager {

    private final UserValidationUtils userValidationUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";
        var user = loginUser(email, password);

        return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getJwtToken(), List.of());
    }

    public UserDto registerUser(UserDto userDto) {
        var errors = userValidationUtils.validateUser(userDto);
        if (!errors.isEmpty()) {
            throw new UserRegistrationException(errors);
        }

        var exists = userRepository.existsByEmail(userDto.getEmail());
        if (exists) {
            throw new UserRegistrationException("User with email '" + userDto.getEmail() + "' already exists");
        }

        var password = passwordEncoder.encode(userDto.getPassword());
        var creationTime = LocalDateTime.now();
        var user = User.builder()
                       .email(userDto.getEmail())
                       .passwordHash(password)
                       .updatedAt(creationTime)
                       .createdAt(creationTime)
                       .lastLogin(creationTime)
                       .resetToken("")
                       .build();

        try {
            var res = userRepository.save(user);
            return UserDto.builder().email(res.getEmail()).build();
        } catch (Exception ex) {
            throw new UserRegistrationException(ex.getMessage());
        }
    }

    public UserDto loginUser(String email, String password) {
        var isValidEmail = userValidationUtils.isEmailValid(email);
        var isValidPassword = userValidationUtils.isPasswordValid(password);
        if (!isValidEmail && !isValidPassword) {
            throw new UserCredentialsValidationException("Email or Password has wrong format");
        }
        var foundUser = userRepository.findByEmail(email);

        if (foundUser.isEmpty()) {
            throw new UserNotFoundException("User with email '" + email + "' not found");
        }

        User user = foundUser.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UserLoginException("Wrong password");
        }

        final String jwt = jwtUtil.generateToken(email);

        user.setLastLogin(LocalDateTime.now());
        userRepository.saveAndFlush(user);

        return UserDto.builder().email(email).jwtToken(jwt).build();
    }

    public void forgotPassword(UserDto userDto) {
        var isValidEmail = userValidationUtils.isEmailValid(userDto.getEmail());
        if (!isValidEmail) {
            throw new UserCredentialsValidationException("Email has wrong format");
        }

        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with email '" + userDto.getEmail() + "' doesn't exist");
        }

        var user = optionalUser.get();
        var existingToken = user.getResetToken();
        var existingTokenTime = user.getResetTokenExpiry();
        var timeNow = LocalDateTime.now();

        if (Objects.nonNull(existingToken) && Objects.nonNull(existingTokenTime) && existingTokenTime.isAfter(timeNow)) {
            var timeToWait = ChronoUnit.MINUTES.between(timeNow, existingTokenTime);
            throw new UserForgotPasswordTooManyRequestsException(
                    "Too many request for password restore. You need to wait: " + timeToWait + " minutes");
        }

        var token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.saveAndFlush(user);
        // TODO: temporal
        System.out.println("Token: " + token);
    }

    public UserDto resetPassword(UserDto resetRequest) {
        var errors = userValidationUtils.validateUser(resetRequest);
        if (!errors.isEmpty()) {
            throw new UserResetPasswordRequiredValuesException(errors);
        }

        if (StringUtils.isBlank(resetRequest.getPasswordResetToken())) {
            throw new UserResetPasswordRequiredValuesException("Password reset token is empty");
        }

        var foundUser = userRepository.findByEmail(resetRequest.getEmail());
        if (foundUser.isEmpty()) {
            throw new UserNotFoundException("User with email '" + resetRequest.getEmail() + "' doesn't exist");
        }

        var user = foundUser.get();

        if (!resetRequest.getPasswordResetToken().equals(user.getResetToken())) {
            throw new UserResetPasswordRequiredValuesException("Password reset token does not match");
        }

        var password = passwordEncoder.encode(resetRequest.getPassword());
        user.setPasswordHash(password);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        var savedUser = userRepository.saveAndFlush(user);

        return UserDto.builder().email(savedUser.getEmail()).build();
    }
}
