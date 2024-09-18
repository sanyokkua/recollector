package ua.kostenko.recollector.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ua.kostenko.recollector.app.exception.JwtTokenException;
import ua.kostenko.recollector.app.service.DateService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtHelperUtilTest {

    @Mock
    private DateService dateService;
    private SecretKey secretKey;
    private JwtHelperUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String testKey = "dewedwd32det723vd772dg17dvtwy2ugdsu23y81gsd1u23dgy2";
        secretKey = Keys.hmacShaKeyFor(testKey.getBytes(StandardCharsets.UTF_8));
        jwtUtil = new JwtHelperUtil(secretKey, secretKey, dateService);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpMinutes", 1);
        ReflectionTestUtils.setField(jwtUtil, "jwtRefreshExpHours", 1);
    }

    @Test
    void generateToken_validUsername_generatesToken() {
        // Arrange
        String username = "testUser";
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 3600000); // 1 hour later
        when(dateService.getAdjustedDateByHours(any(Date.class),
                                                any(Long.class),
                                                any(DateService.Adjuster.class))).thenReturn(expirationDate);

        // Act
        String token = jwtUtil.generateMainJwt(username, currentDate);

        // Assert
        assertNotNull(token);
        assertTrue(StringUtils.isNotBlank(token));
        verify(dateService).getAdjustedDateByHours(currentDate, 1, DateService.Adjuster.MINUTES);
    }

    @Test
    void extractClaims_validToken_returnsClaims() {
        // Arrange
        String username = "testUser";
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 3600000); // 1 hour later
        String token = Jwts.builder()
                           .subject(username)
                           .issuedAt(currentDate)
                           .expiration(expirationDate)
                           .signWith(secretKey)
                           .compact();

        // Act
        Claims extractedClaims = jwtUtil.extractClaimsFromMainJwtToken(token);

        // Assert
        assertNotNull(extractedClaims);
        assertEquals(username, extractedClaims.getSubject());
    }

    @Test
    void extractClaims_invalidToken_throwsJwtTokenException() {
        // Arrange
        String invalidToken = "invalid.token.value";

        // Act & Assert
        assertThrows(JwtTokenException.class, () -> jwtUtil.extractClaimsFromMainJwtToken(invalidToken));
    }

    @Test
    void validateToken_validTokenAndUsername_returnsTrue() {
        // Arrange
        String username = "testUser";
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 3600000); // 1 hour later
        when(dateService.getCurrentDate()).thenReturn(currentDate);

        String token = Jwts.builder()
                           .subject(username)
                           .issuedAt(currentDate)
                           .expiration(expirationDate)
                           .signWith(secretKey)
                           .compact();

        // Act
        boolean isValid = jwtUtil.validateMainJwtToken(token, username);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_invalidUsername_returnsFalse() {
        // Arrange
        String username = "testUser";
        String wrongUsername = "wrongUser";
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 3600000); // 1 hour later
        when(dateService.getCurrentDate()).thenReturn(currentDate);

        String token = Jwts.builder()
                           .subject(username)
                           .issuedAt(currentDate)
                           .expiration(expirationDate)
                           .signWith(secretKey)
                           .compact();

        // Act
        boolean isValid = jwtUtil.validateMainJwtToken(token, wrongUsername);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_expiredToken_returnsFalse() throws InterruptedException {
        // Arrange
        String username = "testUser";
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 1000); // 1sec later

        String token = Jwts.builder()
                           .subject(username)
                           .issuedAt(expirationDate)
                           .expiration(expirationDate)
                           .signWith(secretKey)
                           .compact();
        Thread.sleep(1100);

        // Act
        boolean isValid = jwtUtil.validateMainJwtToken(token, username);

        // Assert
        assertFalse(isValid);
    }
}