package ua.kostenko.recollector.app.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.repository.InvalidatedTokenRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtHelperUtil jwtUtil;

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Initialize any required fields or mock behavior
        Mockito.reset(jwtUtil, request, response, filterChain);
        SecurityContextHolder.getContext().setAuthentication(null);

        jwtRequestFilter = new JwtRequestFilter(jwtUtil, authenticationService, invalidatedTokenRepository);
    }

    @Test
    void doFilterInternal_validToken_authenticatesUser() throws Exception {
        String jwt = "valid.jwt.token";
        String email = "user@example.com";
        User userDetails = mock(User.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractClaimsFromMainJwtToken(jwt)).thenReturn(mock(Claims.class));
        when(jwtUtil.extractClaimsFromMainJwtToken(jwt).getSubject()).thenReturn(email);
        when(jwtUtil.validateMainJwtToken(jwt, email)).thenReturn(true);
        when(authenticationService.findUserByEmail(email)).thenReturn(userDetails);
        when(userDetails.getEmail()).thenReturn(email);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, times(2)).extractClaimsFromMainJwtToken(jwt);
        verify(jwtUtil).validateMainJwtToken(jwt, email);
        verify(authenticationService).findUserByEmail(email);

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
                                                                                                              .getAuthentication();
        User user = (User) auth.getPrincipal();
        assertNotNull(auth);
        assertEquals(email, user.getEmail());
    }

    @Test
    void doFilterInternal_invalidToken_doesNotAuthenticateUser() throws Exception {
        String jwt = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractClaimsFromMainJwtToken(jwt)).thenThrow(new RuntimeException("Invalid JWT"));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractClaimsFromMainJwtToken(jwt);
        verify(jwtUtil, never()).validateMainJwtToken(anyString(), anyString());
        verify(authenticationService, never()).findUserByEmail(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_noToken_doesNotAuthenticateUser() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, never()).extractClaimsFromMainJwtToken(anyString());
        verify(authenticationService, never()).findUserByEmail(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_malformedToken_doesNotAuthenticateUser() throws Exception {
        String jwt = "malformed.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractClaimsFromMainJwtToken(jwt)).thenThrow(new RuntimeException("Malformed JWT"));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractClaimsFromMainJwtToken(jwt);
        verify(jwtUtil, never()).validateMainJwtToken(anyString(), anyString());
        verify(authenticationService, never()).findUserByEmail(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}