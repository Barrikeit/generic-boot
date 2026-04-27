package dev.barrikeit.util;

import dev.barrikeit.util.constants.UtilConstants;
import dev.barrikeit.util.exceptions.UnexpectedException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TimeUtil {

  private static String zone;

  public static Instant instantNow() {
    return Instant.now().atZone(ZoneId.of(zone)).toInstant();
  }

  public static Date dateNow() {
    return Date.from(instantNow());
  }

  public static OffsetDateTime offsetDateTimeNow() {
    return OffsetDateTime.now(ZoneId.of(zone));
  }

  public static OffsetDateTime toOffsetDateTime(Date date) {
    if (date == null) return null;
    return date.toInstant().atZone(ZoneId.of(zone)).toOffsetDateTime();
  }

  public static OffsetDateTime convertOffsetDateTime(String date) {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(UtilConstants.PATTERN_LOCAL_DATE);
    DateTimeFormatter dateTimeFormat =
        DateTimeFormatter.ofPattern(UtilConstants.PATTERN_DATE_TIME).withZone(ZoneId.of(zone));
    try {
      return OffsetDateTime.parse(date, dateTimeFormat);
    } catch (DateTimeParseException e) {
      try {
        return LocalDate.parse(date, dateFormat).atStartOfDay(ZoneId.of(zone)).toOffsetDateTime();
      } catch (DateTimeParseException ex) {
        throw new UnexpectedException("Formato de fecha y hora inválido: " + date);
      }
    }
  }

  public static OffsetDateTime timestampToOffsetDateTime(Timestamp timestamp) {
    return timestamp.toInstant().atZone(ZoneId.of(zone)).toOffsetDateTime();
  }

  public static String formatOffsetDate(OffsetDateTime date) {
    return date.format(DateTimeFormatter.ofPattern(UtilConstants.PATTERN_LOCAL_DATE));
  }

  public static String formatOffsetDateDownload(OffsetDateTime date) {
    return date.format(DateTimeFormatter.ofPattern(UtilConstants.PATTERN_LOCAL_DATE_DOWNLOAD));
  }

  public static String formatOffsetDateTime(OffsetDateTime date) {
    return date.format(DateTimeFormatter.ofPattern(UtilConstants.PATTERN_DATE_TIME_MILLI));
  }

  public static String formatOffsetDateTimeDownload(OffsetDateTime date) {
    return date.format(DateTimeFormatter.ofPattern(UtilConstants.PATTERN_DATE_TIME_DOWNLOAD));
  }

  @Value("${server.timeZone}")
  public void setZoneStatic(String zone) {
    TimeUtil.zone = zone;
  }
}
