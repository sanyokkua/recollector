package ua.kostenko.recollector.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.kostenko.recollector.app.dto.response.Response;

import java.util.List;
import java.util.Objects;

import static ua.kostenko.recollector.app.util.PageRequestUtils.buildMetaInfo;

/**
 * Utility class for constructing {@link ResponseEntity} objects with consistent response formats.
 * Provides methods to build responses for successful data retrieval, paginated data, and error scenarios.
 * <p>
 * The constructor is private to prevent instantiation.
 * <p>
 * Logging is enabled using Lombok's @Slf4j annotation for capturing important events and errors.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseHelper {

    /**
     * Extracts a formatted error message from an exception.
     *
     * @param ex the {@link Exception} from which to extract the error message
     *
     * @return a formatted error message
     */
    private static String getErrorMessage(Exception ex) {
        var type = ex.getClass().getSimpleName();
        var message = ex.getMessage();
        return String.format("%s: %s", type, message);
    }

    /**
     * Builds a {@link ResponseEntity} with a success response.
     * Includes the provided data and HTTP status code.
     *
     * @param data   the data to include in the response body
     * @param status the HTTP status to set for the response
     * @param <T>    the type of the data
     *
     * @return a {@link ResponseEntity} containing the success response
     */
    public static <T> ResponseEntity<Response<T>> buildDtoResponse(T data, HttpStatus status) {
        var responseBody = Response.<T>builder()
                                   .data(data)
                                   .statusCode(status.value())
                                   .statusMessage(status.name())
                                   .build();

        log.debug("Built success response with status {}: {}", status, responseBody);
        return ResponseEntity.status(status).body(responseBody);
    }

    /**
     * Builds a {@link ResponseEntity} with paginated data and meta information.
     * Includes the page data, meta information, and HTTP status code.
     *
     * @param data   the {@link Page} object containing paginated data
     * @param status the HTTP status to set for the response
     * @param <T>    the type of the data in the page
     *
     * @return a {@link ResponseEntity} containing the paginated response
     */
    public static <T> ResponseEntity<Response<List<T>>> buildPageDtoResponse(Page<T> data, HttpStatus status) {
        var metaInfo = buildMetaInfo(data);
        List<T> items = Objects.nonNull(data) ? data.getContent() : List.of();

        var responseBody = Response.<List<T>>builder()
                                   .data(items)
                                   .statusCode(status.value())
                                   .statusMessage(status.name())
                                   .meta(metaInfo)
                                   .build();

        log.debug("Built paginated response with status {}: {}", status, responseBody);
        return ResponseEntity.status(status).body(responseBody);
    }

    /**
     * Builds a {@link ResponseEntity} for error responses.
     * Includes the provided data, error message, and HTTP status code.
     *
     * @param data   the data to include in the response body, can be null
     * @param status the HTTP status to set for the response
     * @param ex     the {@link Exception} causing the error
     * @param <T>    the type of the data
     *
     * @return a {@link ResponseEntity} containing the error response
     */
    public static <T> ResponseEntity<Response<T>> buildDtoErrorResponse(T data, HttpStatus status, Exception ex) {
        var responseBody = createErrorResponseBody(data, status, ex);

        log.error("Built error response with status {}: {}", status, responseBody);
        return ResponseEntity.status(status).body(responseBody);
    }

    public static <T> Response<T> createErrorResponseBody(T data, HttpStatus status, Exception ex) {
        var msg = getErrorMessage(ex);
        return Response.<T>builder()
                       .data(data)
                       .statusCode(status.value())
                       .statusMessage(status.name())
                       .error(msg)
                       .build();
    }
}
