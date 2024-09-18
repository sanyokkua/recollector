package ua.kostenko.recollector.app.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.kostenko.recollector.app.dto.auth.TokensDto;
import ua.kostenko.recollector.app.exception.JwtTokenException;
import ua.kostenko.recollector.app.interfaces.JwtHelper;
import ua.kostenko.recollector.app.service.DateService;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JwtHelperUtil provides utility methods for generating, validating, and extracting claims from JWT tokens.
 * It supports both main and refresh tokens with configurable expiration times.
 */
@Slf4j
@Component
public class JwtHelperUtil implements JwtHelper {

    private static final int MILLISECONDS_IN_SECOND = 1000;

    private final SecretKey jwtMainKey;
    private final SecretKey jwtRefreshKey;
    private final DateService dateService;

    @Value("${recollector.app.jwt.secret.exp}")
    private Integer jwtExpMinutes;

    @Value("${recollector.app.jwt.refresh.exp}")
    private Integer jwtRefreshExpHours;

    public JwtHelperUtil(@Qualifier("jwtSecretKey") SecretKey jwtMainKey,
                         @Qualifier("jwtRefreshSecretKey") SecretKey jwtRefreshKey, DateService dateService) {
        this.jwtMainKey = jwtMainKey;
        this.jwtRefreshKey = jwtRefreshKey;
        this.dateService = dateService;
    }

    /**
     * Extracts claims from the JWT token using the provided secret key.
     *
     * @param token     the JWT token
     * @param secretKey the secret key used to verify the token
     *
     * @return the claims extracted from the token
     */
    private Claims extractClaims(String token, SecretKey secretKey) {
        try {
            JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
            Jws<Claims> signedClaims = parser.parseSignedClaims(token);
            return signedClaims.getPayload();
        } catch (ExpiredJwtException ex) {
            // Handle the expired token case
            return ex.getClaims();
        } catch (RuntimeException ex) {
            log.error("Failed to extract claims from token: {}", ex.getMessage(), ex);
            throw new JwtTokenException("Invalid JWT token: " + ex.getMessage());
        }
    }

    /**
     * Checks if the JWT token is expired based on the claims.
     *
     * @param claims the claims extracted from the token
     *
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(Claims claims) {
        Date expirationDate = claims.getExpiration();
        Date currentDate = dateService.getCurrentDate();

        if (expirationDate == null || currentDate == null) {
            log.warn("Token expiration date or current date is null.");
            return true;
        }

        return expirationDate.before(currentDate);
    }

    /**
     * Validates the JWT token by matching the username and checking if it's expired.
     *
     * @param token     the JWT token
     * @param username  the username to validate
     * @param secretKey the secret key used to verify the token
     *
     * @return true if the token is valid, false otherwise
     */
    private boolean validateToken(String token, String username, SecretKey secretKey) {
        if (token == null || username == null) {
            log.warn("Token or username is null for validation.");
            return false;
        }

        try {
            Claims claims = extractClaims(token, secretKey);
            String claimUsername = claims.getSubject();

            if (claimUsername == null) {
                log.warn("Username claim is null.");
                return false;
            }

            boolean isUsernameValid = username.equals(claimUsername);
            boolean isTokenValid = !isTokenExpired(claims);

            return isUsernameValid && isTokenValid;
        } catch (JwtTokenException ex) {
            log.warn("JWT token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Generates a JWT token for the provided username, start date, and expiration settings.
     *
     * @param username  the username for the token
     * @param startDate the date when the token is issued
     * @param time      the expiration time in hours or minutes
     * @param adjuster  the adjuster for setting the expiration time
     * @param key       the secret key used to sign the token
     *
     * @return the generated JWT token
     */
    private String generateJwtToken(String username, Date startDate, int time, DateService.Adjuster adjuster,
                                    SecretKey key) {
        Date expirationDate = dateService.getAdjustedDateByHours(startDate, time, adjuster);

        return Jwts.builder().subject(username).issuedAt(startDate).expiration(expirationDate).signWith(key).compact();
    }

    /**
     * Generates a JWT token for the main authentication process using the provided username and current time.
     *
     * @param username the username for the token
     * @param timeNow  the current date and time when the token is issued
     *
     * @return the generated main JWT token
     */
    @Override
    public String generateMainJwt(String username, Date timeNow) {
        return generateJwtToken(username, timeNow, jwtExpMinutes, DateService.Adjuster.MINUTES, jwtMainKey);
    }

    /**
     * Generates a JWT token for the refresh process using the provided username and current time.
     *
     * @param username the username for the token
     * @param timeNow  the current date and time when the token is issued
     *
     * @return the generated refresh JWT token
     */
    @Override
    public String generateRefreshJwt(String username, Date timeNow) {
        return generateJwtToken(username, timeNow, jwtRefreshExpHours, DateService.Adjuster.HOURS, jwtRefreshKey);
    }

    /**
     * Generates a pair of JWT tokens (main and refresh) for the specified username.
     *
     * @param username the username for the tokens
     *
     * @return a TokensDto containing the generated tokens and their expiration times
     */
    @Override
    public TokensDto generateJwtTokensPair(String username) {
        Date now = dateService.getCurrentDate();

        String jwtToken = generateMainJwt(username, now);
        String refreshToken = generateRefreshJwt(username, now);

        int jwtTokenExpiration = (int) extractClaimsFromMainJwtToken(jwtToken).getExpiration()
                                                                              .getTime() / MILLISECONDS_IN_SECOND;
        int refreshTokenExpiration = (int) extractClaimsFromRefreshJwtToken(refreshToken).getExpiration()
                                                                                         .getTime() / MILLISECONDS_IN_SECOND;

        return TokensDto.builder()
                        .jwtToken(jwtToken)
                        .refreshToken(refreshToken)
                        .jwtTokenExpirationDate(jwtTokenExpiration)
                        .jwtRefreshTokenExpirationDate(refreshTokenExpiration)
                        .build();
    }

    /**
     * Validates the main JWT token by matching the username and checking if it's expired.
     *
     * @param token    the JWT token
     * @param username the username to validate
     *
     * @return true if the token is valid, false otherwise
     */
    @Override
    public boolean validateMainJwtToken(String token, String username) {
        return validateToken(token, username, jwtMainKey);
    }

    /**
     * Validates the refresh JWT token by matching the username and checking if it's expired.
     *
     * @param token    the JWT token
     * @param username the username to validate
     *
     * @return true if the token is valid, false otherwise
     */
    @Override
    public boolean validateRefreshJwtToken(String token, String username) {
        return validateToken(token, username, jwtRefreshKey);
    }

    /**
     * Extracts claims from the main JWT token using the main secret key.
     *
     * @param token the JWT token
     *
     * @return the claims extracted from the main JWT token
     */
    @Override
    public Claims extractClaimsFromMainJwtToken(String token) {
        return extractClaims(token, jwtMainKey);
    }

    /**
     * Extracts claims from the refresh JWT token using the refresh secret key.
     *
     * @param token the JWT token
     *
     * @return the claims extracted from the refresh JWT token
     */
    @Override
    public Claims extractClaimsFromRefreshJwtToken(String token) {
        return extractClaims(token, jwtRefreshKey);
    }
}
