package ua.kostenko.recollector.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.kostenko.recollector.app.dto.response.Response;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseHelper {

    private static String getErrorMessage(Exception ex) {
        var type = ex.getClass().getSimpleName();
        var message = ex.getMessage();
        return String.format("%s: %s", type, message);
    }

    public static <T> ResponseEntity<Response<T>> buildDtoResponse(T data, HttpStatus status) {
        var responseBody = Response.<T>builder()
                                   .data(data)
                                   .statusCode(status.value())
                                   .statusMessage(status.name())
                                   .build();
        return ResponseEntity.status(status).body(responseBody);
    }

    public static <T> ResponseEntity<Response<T>> buildDtoErrorResponse(T data, HttpStatus status, Exception ex) {
        var msg = getErrorMessage(ex);
        var statusCode = status.value();
        var statusMessage = status.name();

        var responseBody = Response.<T>builder()
                                   .data(data)
                                   .statusCode(statusCode)
                                   .statusMessage(statusMessage)
                                   .error(msg)
                                   .build();
        return ResponseEntity.status(status).body(responseBody);
    }
}
