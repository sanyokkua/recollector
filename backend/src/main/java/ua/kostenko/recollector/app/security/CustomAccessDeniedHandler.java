package ua.kostenko.recollector.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ua.kostenko.recollector.app.util.ResponseHelper;

import java.io.IOException;

/**
 * CustomAccessDeniedHandler handles access denied errors and sends a JSON response.
 * It implements the AccessDeniedHandler interface to provide custom behavior
 * when a user attempts to access a resource without proper permissions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Handles access denied exceptions by sending a JSON response with a
     * forbidden status code (403) and an error message.
     *
     * @param request               the HttpServletRequest
     * @param response              the HttpServletResponse
     * @param accessDeniedException the AccessDeniedException that triggered this handler
     *
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        // Log access denied details for debugging purposes
        log.warn("Access denied for request {}. Reason: {}",
                 request.getRequestURI(),
                 accessDeniedException.getMessage());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        var errorResponse = ResponseHelper.createErrorResponseBody(null, HttpStatus.FORBIDDEN, accessDeniedException);

        // Write JSON response to output stream
        response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
    }
}
