package ua.kostenko.recollector.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.kostenko.recollector.app.exception.JwtTokenException;
import ua.kostenko.recollector.app.service.DateService;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for handling JWT operations such as generating and validating tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final SecretKey secretKey;
    private final DateService dateService;

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username for which the token is generated
     *
     * @return the generated JWT token
     */
    public String generateToken(String username) {
        log.info("Generating token for username: {}", username);
        Date currentDate = dateService.getCurrentDate();
        Date expirationDate = dateService.getAdjustedDateByHours(currentDate, 1, DateService.Adjuster.HOURS);
        return Jwts.builder()
                   .subject(username).issuedAt(currentDate).expiration(expirationDate)
                   .signWith(secretKey)
                   .compact();
    }

    /**
     * Extracts claims from the given JWT token.
     *
     * @param token the JWT token from which claims are extracted
     *
     * @return the claims extracted from the token
     */
    public Claims extractClaims(String token) {
        log.debug("Extracting claims from token: {}", token);
        try {
            JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
            Jws<Claims> signedClaims = parser.parseSignedClaims(token);
            return signedClaims.getPayload();
        } catch (RuntimeException ex) {
            throw new JwtTokenException(ex.getMessage());
        }
    }

    /**
     * Validates the given JWT token against the provided username.
     *
     * @param token    the JWT token to be validated
     * @param username the username to validate against the token
     *
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token, String username) {
        log.info("Validating token for username: {}", username);
        try {
            String claimUsername = extractClaims(token).getSubject();
            boolean isCorrectName = username.equals(claimUsername);
            boolean isValidToken = !isTokenExpired(token);
            return isCorrectName && isValidToken;
        } catch (RuntimeException ex) {
            log.warn("Failed to validate token", ex);
            return false;
        }
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param token the JWT token to check for expiration
     *
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        log.debug("Checking if token is expired: {}", token);
        try {
            Date expirationDate = extractClaims(token).getExpiration();
            Date currentDate = dateService.getCurrentDate();
            return expirationDate.before(currentDate);
        } catch (RuntimeException ex) {
            log.warn("Failed check of token expiration", ex);
            return true;
        }
    }
}
