package ua.kostenko.recollector.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import ua.kostenko.recollector.app.dto.StatisticDto;
import ua.kostenko.recollector.app.exception.UserNotAuthenticatedException;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.security.JwtUtil;
import ua.kostenko.recollector.app.service.HelperService;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelperController.class)
@AutoConfigureMockMvc(addFilters = false)
class HelperControllerTest {

    private static final String BASE_URL = "/api/v1/helper";
    private static final String VALID_EMAIL = "valid@email.com";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private AuthService authService;
    @MockBean
    private HelperService helperService;

    @Test
    void getItemStatuses_getRequest_shouldReturnListOfStatuses() throws Exception {
        // Arrange
        List<String> expectedStatuses = List.of("FINISHED", "IN_PROGRESS", "TODO_LATER");
        when(helperService.getItemStatuses()).thenReturn(expectedStatuses);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/itemStatuses"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(200))
               .andExpect(jsonPath("$.statusMessage").value("OK"))
               .andExpect(jsonPath("$.data").isArray())
               .andExpect(jsonPath("$.data").value(containsInAnyOrder("FINISHED", "IN_PROGRESS", "TODO_LATER")))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Test
    void getStatistics_getRequest_shouldReturnStatisticsDto() throws Exception {
        // Arrange
        StatisticDto expectedStatistics = StatisticDto.builder()
                                                      .totalNumberOfCategories(5)
                                                      .totalNumberOfItems(20)
                                                      .totalNumberOfItemsTodo(10)
                                                      .totalNumberOfItemsInProgress(5)
                                                      .totalNumberOfItemsFinished(5)
                                                      .build();

        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(helperService.getStatistics(VALID_EMAIL)).thenReturn(expectedStatistics);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/statistics"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(200))
               .andExpect(jsonPath("$.statusMessage").value("OK"))
               .andExpect(jsonPath("$.data.totalNumberOfCategories").value(5))
               .andExpect(jsonPath("$.data.totalNumberOfItems").value(20))
               .andExpect(jsonPath("$.data.totalNumberOfItemsTodo").value(10))
               .andExpect(jsonPath("$.data.totalNumberOfItemsInProgress").value(5))
               .andExpect(jsonPath("$.data.totalNumberOfItemsFinished").value(5))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Test
    void getStatistics_getRequestWithMissingEmail_shouldReturnUnauthorized() throws Exception {
        // Arrange
        when(authService.getUserEmailFromAuthContext()).thenThrow(new UserNotAuthenticatedException(
                "User not authenticated"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/statistics"))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.UNAUTHORIZED.name()))
               .andExpect(jsonPath("$.data").doesNotExist())
               .andExpect(jsonPath("$.error").value("UserNotAuthenticatedException: User not authenticated"))
               .andDo(print());
    }
}