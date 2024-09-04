package ua.kostenko.recollector.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kostenko.recollector.app.dto.StatisticDto;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.service.HelperService;
import ua.kostenko.recollector.app.util.ResponseHelper;

import java.util.List;

/**
 * REST controller for helper-related operations.
 * Provides endpoints for retrieving item statuses and user-specific statistics.
 */
@RestController
@RequestMapping("api/v1/helper")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Helper Operations", description = "Endpoints for retrieving helper information such as item statuses and user statistics.")
public class HelperController {

    private final HelperService helperService;
    private final AuthService authService;

    /**
     * Retrieves the list of available item statuses.
     *
     * @return a {@link ResponseEntity} with the list of item statuses and HTTP status {@code 200 OK}.
     */
    @Operation(summary = "Retrieve item statuses", description = "Retrieves a list of available item statuses.")
    @GetMapping("/itemStatuses")
    public ResponseEntity<Response<List<String>>> getItemStatuses() {
        log.info("Fetching item statuses");
        var statuses = helperService.getItemStatuses();
        return ResponseHelper.buildDtoResponse(statuses, HttpStatus.OK);
    }

    /**
     * Retrieves user-specific statistics based on the authenticated user's email.
     *
     * @return a {@link ResponseEntity} with the user's statistics and HTTP status {@code 200 OK}.
     */
    @Operation(summary = "Retrieve user statistics", description = "Retrieves statistics for the authenticated user based on their email.")
    @GetMapping("/statistics")
    public ResponseEntity<Response<StatisticDto>> getStatistics() {
        var email = authService.getUserEmailFromAuthContext();
        log.info("Fetching statistics for user with email: {}", email);
        var statistics = helperService.getStatistics(email);
        return ResponseHelper.buildDtoResponse(statistics, HttpStatus.OK);
    }
}
