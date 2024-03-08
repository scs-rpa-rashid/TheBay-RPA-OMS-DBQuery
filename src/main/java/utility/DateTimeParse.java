package utility;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeParse {
    static LocalTime localTime;
    static LocalDateTime datetime;
    static Instant instant;
    static ZonedDateTime estZoneCurrentTime;
    static LocalDateTime estLocalDateCurrentTime;

    public static LocalDateTime GetTime(String inputTime) {
        localTime = LocalTime.parse(inputTime,
                DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));
        datetime = localTime.atDate(LocalDate.now());
        return datetime;
    }
    public static LocalDateTime GetCurrentDateTime() {
        //get current time utc time
        instant = Instant.now();
        //System.out.println("Instant time: "+instant);
        //convert to EST time zone
        estZoneCurrentTime = instant.atZone(ZoneId.of(Constants.TIME_ZONE));
        estLocalDateCurrentTime = estZoneCurrentTime.toLocalDateTime();
        return estLocalDateCurrentTime;
    }
}
