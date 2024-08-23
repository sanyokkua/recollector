package ua.kostenko.recollector.app.controller;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("api/v1/helper")
@RequiredArgsConstructor
public class HelperController {

    private final HelperService helperService;
    private final AuthService authService;

    @GetMapping("/itemStatuses")
    public ResponseEntity<Response<List<String>>> getItemStatuses() {
        var statuses = helperService.getItemStatuses();
        return ResponseHelper.buildDtoResponse(statuses, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Response<StatisticDto>> getStatistics() {
        var email = authService.getUserEmailFromAuthContext();
        var statistics = helperService.getStatistics(email);
        return ResponseHelper.buildDtoResponse(statistics, HttpStatus.OK);
    }
}
