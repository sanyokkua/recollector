package ua.kostenko.recollector.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class DateService {

    public Date getCurrentDate() {
        var timeNow = LocalDateTime.now();
        var instantNow = timeNow.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instantNow);
    }

    public Date getAdjustedDateByHours(Date date, long to, Adjuster adjuster) {
        if (Objects.isNull(date) || Objects.isNull(adjuster)) {
            return date;
        }

        var localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        var adjusted = switch (adjuster) {
            case HOURS -> localDateTime.plusHours(to);
            case MINUTES -> localDateTime.plusMinutes(to);
            case SECONDS -> localDateTime.plusSeconds(to);
        };

        Instant instantNow = adjusted.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instantNow);
    }

    public Date getDateFromLocalDateTime(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    public enum Adjuster {
        HOURS,
        MINUTES,
        SECONDS
    }
}
