package ua.kostenko.recollector.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DateServiceTest {

    private DateService dateService;

    @BeforeEach
    void setUp() {
        dateService = new DateService();
    }

    @Test
    void getCurrentDate_noInput_returnsCurrentDate() {
        Date currentDate = dateService.getCurrentDate();

        assertNotNull(currentDate);
    }

    @Test
    void getAdjustedDateByHours_nullDate_returnsNull() {
        Date result = dateService.getAdjustedDateByHours(null, 1, DateService.Adjuster.HOURS);

        assertNull(result);
    }

    @Test
    void getAdjustedDateByHours_nullAdjuster_returnsSameDate() {
        Date date = new Date();
        Date result = dateService.getAdjustedDateByHours(date, 1, null);

        assertEquals(date, result);
    }

    @Test
    void getAdjustedDateByHours_validDateAndHours_returnsAdjustedDate() {
        Date date = new Date();
        Date result = dateService.getAdjustedDateByHours(date, 2, DateService.Adjuster.HOURS);
        LocalDateTime expectedDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusHours(2);
        Date expectedDate = Date.from(expectedDateTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(expectedDate, result);
    }

    @Test
    void getAdjustedDateByHours_validDateAndMinutes_returnsAdjustedDate() {
        Date date = new Date();
        Date result = dateService.getAdjustedDateByHours(date, 30, DateService.Adjuster.MINUTES);
        LocalDateTime expectedDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                                                      .plusMinutes(30);
        Date expectedDate = Date.from(expectedDateTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(expectedDate, result);
    }

    @Test
    void getAdjustedDateByHours_validDateAndSeconds_returnsAdjustedDate() {
        Date date = new Date();
        Date result = dateService.getAdjustedDateByHours(date, 45, DateService.Adjuster.SECONDS);
        LocalDateTime expectedDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                                                      .plusSeconds(45);
        Date expectedDate = Date.from(expectedDateTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(expectedDate, result);
    }

    @Test
    void getDateFromLocalDateTime_validLocalDateTime_returnsDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date result = dateService.getDateFromLocalDateTime(localDateTime);
        Date expectedDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(expectedDate, result);
    }
}