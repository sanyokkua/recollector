package ua.kostenko.recollector.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.dto.StatisticDto;
import ua.kostenko.recollector.app.dto.UserSettingsDto;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.security.AuthenticationService;
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
    private final AuthenticationService authService;

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
        var email = authService.getUserFromAuthContext();
        log.info("Fetching statistics for user with email: {}", email);
        var statistics = helperService.getStatistics(email.getEmail());
        return ResponseHelper.buildDtoResponse(statistics, HttpStatus.OK);
    }

    /**
     * Retrieves user-specific settings based on the authenticated user's email.
     *
     * @return a {@link ResponseEntity} with the user's settings and HTTP status {@code 200 OK}.
     */
    @Operation(summary = "Retrieve user settings", description = "Retrieves settings for the authenticated user based on their email.")
    @GetMapping("/settings")
    public ResponseEntity<Response<UserSettingsDto>> getUserSettings() {
        var email = authService.getUserFromAuthContext();
        log.info("Fetching settings for user with email: {}", email);
        var settings = helperService.getUserSettings(email.getEmail());
        return ResponseHelper.buildDtoResponse(settings, HttpStatus.OK);
    }

    /**
     * Retrieves user-specific settings based on the authenticated user's email.
     *
     * @return a {@link ResponseEntity} with the user's settings and HTTP status {@code 200 OK}.
     */
    @Operation(summary = "Update user settings", description = "Updates settings for the authenticated user based on their email.")
    @PutMapping("/settings")
    public ResponseEntity<Response<UserSettingsDto>> updateUserSettings(
            @RequestBody @Parameter(description = "Updated userSettings details") UserSettingsDto userSettingsDto) {
        var email = authService.getUserFromAuthContext();
        log.info("Updating settings for user with email: {}", email);
        var settings = helperService.saveUserSettings(email.getEmail(), userSettingsDto);
        return ResponseHelper.buildDtoResponse(settings, HttpStatus.OK);
    }
}
