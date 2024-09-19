package ua.kostenko.recollector.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * Service class for handling date and time operations.
 * Provides methods for getting the current date, adjusting dates, and converting between
 * {@link LocalDateTime} and {@link Date}.
 */
@Slf4j
@Service
public class DateService {

    /**
     * Gets the current date and time from the system default zone.
     *
     * @return the current date and time as a {@link Date} object
     */
    public Date getCurrentDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date currentDate = Date.from(instant);
        log.debug("Retrieved current date: {}", currentDate);
        return currentDate;
    }

    /**
     * Adjusts the given date by the specified amount of time based on the provided adjuster.
     *
     * @param date     the initial date to be adjusted
     * @param amount   the amount of time to adjust the date by (in hours, minutes, or seconds)
     * @param adjuster the unit of time to adjust (HOURS, MINUTES, or SECONDS)
     *
     * @return the adjusted date as a {@link Date} object
     */
    public Date getAdjustedDateByHours(Date date, long amount, Adjuster adjuster) {
        if (Objects.isNull(date) || Objects.isNull(adjuster)) {
            log.warn("Date or Adjuster is null. Returning the original date.");
            return date;
        }

        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime adjustedDateTime;

        switch (adjuster) {
            case HOURS:
                adjustedDateTime = localDateTime.plusHours(amount);
                break;
            case MINUTES:
                adjustedDateTime = localDateTime.plusMinutes(amount);
                break;
            case SECONDS:
                adjustedDateTime = localDateTime.plusSeconds(amount);
                break;
            default:
                log.error("Invalid Adjuster type provided: {}", adjuster);
                throw new IllegalArgumentException("Invalid Adjuster type.");
        }

        Instant instant = adjustedDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date adjustedDate = Date.from(instant);
        log.debug("Adjusted date: {} by {} {}", adjustedDate, amount, adjuster);
        return adjustedDate;
    }

    /**
     * Converts a {@link LocalDateTime} to a {@link Date} using the system default zone.
     *
     * @param localDateTime the {@link LocalDateTime} to be converted
     *
     * @return the corresponding {@link Date} object
     */
    public Date getDateFromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            log.warn("Provided LocalDateTime is null. Returning null.");
            return null;
        }

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        log.debug("Converted LocalDateTime to Date: {}", date);
        return date;
    }

    /**
     * Enumeration for specifying units of time for date adjustment.
     */
    public enum Adjuster {
        HOURS,
        MINUTES,
        SECONDS
    }
}
