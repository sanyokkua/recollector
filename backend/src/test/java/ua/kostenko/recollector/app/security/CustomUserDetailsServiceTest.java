package ua.kostenko.recollector.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private AuthService authService;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.reset(authService);
        customUserDetailsService = new CustomUserDetailsService(authService);
    }

    @Test
    void loadUserByUsername_userFound_returnsUserDetails() {
        String email = "test@example.com";
        String passwordHash = "encodedPassword";
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        when(authService.findUserByEmail(email)).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
        assertEquals(passwordHash, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        String email = "nonexistent@example.com";

        when(authService.findUserByEmail(email)).thenThrow(new UserNotFoundException(
                "User with email 'nonexistent@example.com' not found"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                                                       () -> customUserDetailsService.loadUserByUsername(email));

        assertEquals("User with email 'nonexistent@example.com' not found", exception.getMessage());
    }
}