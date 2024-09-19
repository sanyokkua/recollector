package ua.kostenko.recollector.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.repository.InvalidatedTokenRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Filter to intercept requests and validate JWT tokens.
 * If a valid JWT is found, the user is authenticated and added to the SecurityContext.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtHelperUtil jwtUtil;
    private final AuthenticationService authenticationService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    /**
     * Filters incoming HTTP requests to check for a JWT token in the Authorization header.
     * If a valid JWT token is found, the corresponding user is authenticated.
     *
     * @param request     the HttpServletRequest object.
     * @param response    the HttpServletResponse object.
     * @param filterChain the FilterChain object.
     *
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an input or output error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        // Log the incoming request URI for tracing
        log.debug("Processing request for URI: {}", request.getRequestURI());

        // Check if the Authorization header contains a Bearer token
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractClaimsFromMainJwtToken(jwt).getSubject();
                log.debug("Extracted email '{}' from JWT token", email);
            } catch (Exception e) {
                log.warn("Failed to extract claims from JWT token: {}", e.getMessage());
            }
        } else {
            log.debug("No JWT token found in the Authorization header");
        }

        // Authenticate the user if the JWT is valid and the user is not already authenticated
        if (Objects.nonNull(email) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            User foundUser = authenticationService.findUserByEmail(email);

            if (jwtUtil.validateMainJwtToken(jwt, foundUser.getEmail())) {
                var tokenInDb = invalidatedTokenRepository.findByUser_UserIdAndToken(foundUser.getUserId(), jwt);
                var isTokenNotInBlackList = tokenInDb.isEmpty();

                if (isTokenNotInBlackList) {
                    log.debug("JWT token is valid for user '{}'", email);
                    var passwordHash = foundUser.getPasswordHash();
                    var details = new WebAuthenticationDetailsSource().buildDetails(request);
                    var authenticationToken = new UsernamePasswordAuthenticationToken(foundUser,
                                                                                      passwordHash,
                                                                                      List.of());
                    authenticationToken.setDetails(details);

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("User '{}' authenticated successfully", email);
                }
            } else {
                log.warn("Invalid JWT token for user '{}'", email);
            }
        } else {
            log.debug("User '{}' is not authenticated or no valid JWT token provided", email);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
