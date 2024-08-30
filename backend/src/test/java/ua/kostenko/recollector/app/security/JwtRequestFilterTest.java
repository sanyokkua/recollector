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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

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
        Mockito.reset(jwtUtil, userDetailsService, request, response, filterChain);
        SecurityContextHolder.getContext().setAuthentication(null);

        jwtRequestFilter = new JwtRequestFilter(jwtUtil, userDetailsService);
    }

    @Test
    void doFilterInternal_validToken_authenticatesUser() throws Exception {
        String jwt = "valid.jwt.token";
        String email = "user@example.com";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractClaims(jwt)).thenReturn(mock(Claims.class));
        when(jwtUtil.extractClaims(jwt).getSubject()).thenReturn(email);
        when(jwtUtil.validateToken(jwt, email)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, times(2)).extractClaims(jwt);
        verify(jwtUtil).validateToken(jwt, email);
        verify(userDetailsService).loadUserByUsername(email);

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
                                                                                                              .getAuthentication();

        assertNotNull(auth);
        assertEquals(email, auth.getName());
        assertEquals(userDetails.getAuthorities(), auth.getAuthorities());
    }

    @Test
    void doFilterInternal_invalidToken_doesNotAuthenticateUser() throws Exception {
        String jwt = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractClaims(jwt)).thenThrow(new RuntimeException("Invalid JWT"));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractClaims(jwt);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_noToken_doesNotAuthenticateUser() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, never()).extractClaims(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_malformedToken_doesNotAuthenticateUser() throws Exception {
        String jwt = "malformed.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractClaims(jwt)).thenThrow(new RuntimeException("Malformed JWT"));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractClaims(jwt);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}