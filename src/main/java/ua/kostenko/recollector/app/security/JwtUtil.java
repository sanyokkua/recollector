package ua.kostenko.recollector.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for handling JWT operations such as generating and validating tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    /**
     * Constant representing one hour in milliseconds.
     */
    public static final int ONE_HOUR = 3600000;
    private final SecretKey secretKey;

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username for which the token is generated
     *
     * @return the generated JWT token
     */
    public String generateToken(String username) {
        log.info("Generating token for username: {}", username);
        return Jwts.builder()
                   .subject(username)
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + ONE_HOUR))
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
        JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
        return parser.parseSignedClaims(token).getPayload();
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
        return username.equals(extractClaims(token).getSubject()) && !isTokenExpired(token);
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
        return extractClaims(token).getExpiration().before(new Date());
    }
}
