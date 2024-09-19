package ua.kostenko.recollector.app.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.kostenko.recollector.app.dto.response.Response;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseHelperTest {

    @Test
    void buildDtoResponse_whenValidData_returnsSuccessResponse() {
        // Arrange
        String data = "Test data";
        HttpStatus status = HttpStatus.OK;

        // Act
        ResponseEntity<Response<String>> responseEntity = ResponseHelper.buildDtoResponse(data, status);

        // Assert
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        Response<String> responseBody = responseEntity.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(status.value(), responseBody.getStatusCode(), "Status code should match");
        assertEquals(status.name(), responseBody.getStatusMessage(), "Status message should match");
        assertEquals(data, responseBody.getData(), "Response data should match");
        assertNull(responseBody.getMeta(), "Meta information should be null");
        assertNull(responseBody.getError(), "Error should be null");
    }

    @Test
    void buildDtoResponse_whenDataIsNull_returnsSuccessResponseWithNullData() {
        // Arrange
        HttpStatus status = HttpStatus.NO_CONTENT;

        // Act
        ResponseEntity<Response<String>> responseEntity = ResponseHelper.buildDtoResponse(null, status);

        // Assert
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        Response<String> responseBody = responseEntity.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(status.value(), responseBody.getStatusCode(), "Status code should match");
        assertEquals(status.name(), responseBody.getStatusMessage(), "Status message should match");
        assertNull(responseBody.getData(), "Response data should be null");
        assertNull(responseBody.getMeta(), "Meta information should be null");
        assertNull(responseBody.getError(), "Error should be null");
    }

    @Test
    void buildPageDtoResponse_whenPageHasData_returnsPageResponse() {
        // Arrange
        List<String> content = List.of("item1",
                                       "item2",
                                       "item3",
                                       "item4",
                                       "item5",
                                       "item6",
                                       "item7",
                                       "item8",
                                       "item9",
                                       "item10");
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 10, Sort.by("name").ascending()), content.size());
        HttpStatus status = HttpStatus.OK;

        // Act
        ResponseEntity<Response<List<String>>> responseEntity = ResponseHelper.buildPageDtoResponse(page, status);

        // Assert
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        Response<List<String>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(status.value(), responseBody.getStatusCode(), "Status code should match");
        assertEquals(status.name(), responseBody.getStatusMessage(), "Status message should match");
        assertEquals(content, responseBody.getData(), "Response data should match");
        assertNotNull(responseBody.getMeta(), "Meta information should not be null");
        assertEquals(1, responseBody.getMeta().getPagination().getCurrentPage(), "Current page should be 1");
        assertEquals(10, responseBody.getMeta().getPagination().getItemsPerPage(), "Items per page should be 10");
        assertEquals(1, responseBody.getMeta().getPagination().getTotalPages(), "Total pages should be 1");
        assertEquals(10, responseBody.getMeta().getPagination().getTotalItems(), "Total items should be 10");
        assertEquals("name", responseBody.getMeta().getPagination().getSortField(), "Sort field should be 'name'");
        assertEquals("ASC",
                     responseBody.getMeta().getPagination().getSortDirection(),
                     "Sort direction should be 'ASC'");
    }

    @Test
    void buildPageDtoResponse_whenPageIsEmpty_returnsPageResponseWithEmptyData() {
        // Arrange
        Page<String> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        HttpStatus status = HttpStatus.NO_CONTENT;

        // Act
        ResponseEntity<Response<List<String>>> responseEntity = ResponseHelper.buildPageDtoResponse(page, status);

        // Assert
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        Response<List<String>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(status.value(), responseBody.getStatusCode(), "Status code should match");
        assertEquals(status.name(), responseBody.getStatusMessage(), "Status message should match");
        assertTrue(responseBody.getData().isEmpty(), "Response data should be empty");
        assertNotNull(responseBody.getMeta(), "Meta information should not be null");
        assertEquals(1, responseBody.getMeta().getPagination().getCurrentPage(), "Current page should be 1");
        assertEquals(10, responseBody.getMeta().getPagination().getItemsPerPage(), "Items per page should be 1-");
        assertEquals(0, responseBody.getMeta().getPagination().getTotalPages(), "Total pages should be 0");
        assertEquals(0, responseBody.getMeta().getPagination().getTotalItems(), "Total items should be 0");
        assertEquals("", responseBody.getMeta().getPagination().getSortField(), "Sort field should be empty");
        assertEquals("", responseBody.getMeta().getPagination().getSortDirection(), "Sort direction should be empty");
    }

    @Test
    void buildPageDtoResponse_whenPageIsNull_returnsErrorResponse() {
        // Arrange
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Act
        ResponseEntity<Response<List<String>>> responseEntity = ResponseHelper.buildPageDtoResponse(null, status);

        // Assert
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        Response<List<String>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(status.value(), responseBody.getStatusCode(), "Status code should match");
        assertEquals(status.name(), responseBody.getStatusMessage(), "Status message should match");
        assertNotNull(responseBody.getData(), "Response data should not be null");
        assertNotNull(responseBody.getMeta(), "Meta information should not be null");
        assertNull(responseBody.getError(), "Error should be null");
    }

    @Test
    void buildDtoErrorResponse_whenExceptionOccurs_returnsErrorResponse() {
        // Arrange
        String data = "Error data";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Exception ex = new RuntimeException("Test exception");

        // Act
        ResponseEntity<Response<String>> responseEntity = ResponseHelper.buildDtoErrorResponse(data, status, ex);

        // Assert
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        Response<String> responseBody = responseEntity.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(status.value(), responseBody.getStatusCode(), "Status code should match");
        assertEquals(status.name(), responseBody.getStatusMessage(), "Status message should match");
        assertEquals(data, responseBody.getData(), "Response data should match");
        assertEquals("RuntimeException: Test exception", responseBody.getError(), "Error message should match");
    }

    @Test
    void buildDtoErrorResponse_whenDataIsNull_returnsErrorResponseWithNullData() {
        // Arrange
        String data = null;
        HttpStatus status = HttpStatus.NOT_FOUND;
        Exception ex = new IllegalArgumentException("Not found");

        // Act
        ResponseEntity<Response<String>> responseEntity = ResponseHelper.buildDtoErrorResponse(data, status, ex);

        // Assert
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        Response<String> responseBody = responseEntity.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(status.value(), responseBody.getStatusCode(), "Status code should match");
        assertEquals(status.name(), responseBody.getStatusMessage(), "Status message should match");
        assertNull(responseBody.getData(), "Response data should be null");
        assertEquals("IllegalArgumentException: Not found", responseBody.getError(), "Error message should match");
    }

    @Test
    void formatErrorMessage_whenExceptionOccurs_returnsFormattedErrorMessage() throws Exception {
        // Arrange
        Method method = ResponseHelper.class.getDeclaredMethod("getErrorMessage", Exception.class);
        method.setAccessible(true);
        Exception ex = new RuntimeException("Test exception");

        // Act
        String result = (String) method.invoke(null, ex);

        // Assert
        assertEquals("RuntimeException: Test exception", result, "Formatted error message should match");
    }
}
