package ua.kostenko.recollector.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ua.kostenko.recollector.app.util.ResponseHelper;

import java.io.IOException;

/**
 * CustomAuthenticationEntryPoint handles authentication errors by sending
 * a JSON response with an unauthorized status code (401) and an error message.
 * It implements the AuthenticationEntryPoint interface to provide custom
 * behavior when authentication fails.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Handles authentication exceptions by sending a JSON response with an
     * unauthorized status code (401) and an error message.
     *
     * @param request       the HttpServletRequest
     * @param response      the HttpServletResponse
     * @param authException the AuthenticationException that triggered this handler
     *
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Log authentication failure details for debugging purposes
        log.warn("Authentication failed for request {}. Reason: {}",
                 request.getRequestURI(),
                 authException.getMessage());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        var errorResponse = ResponseHelper.createErrorResponseBody(null, HttpStatus.UNAUTHORIZED, authException);

        // Write JSON response to output stream
        response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
    }
}