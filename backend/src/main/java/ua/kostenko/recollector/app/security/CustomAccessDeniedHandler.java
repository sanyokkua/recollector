package ua.kostenko.recollector.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ua.kostenko.recollector.app.util.ResponseHelper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper jacksonObjectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        var responseResponseEntity = ResponseHelper.createErrorResponseBody(null,
                                                                            HttpStatus.FORBIDDEN,
                                                                            accessDeniedException);
        response.getOutputStream().println(jacksonObjectMapper.writeValueAsString(responseResponseEntity));
    }
}
