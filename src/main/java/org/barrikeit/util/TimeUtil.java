package org.barrikeit.util;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import org.barrikeit.util.constants.UtilConstants;
import org.barrikeit.util.exceptions.UnExpectedException;
import org.springframework.beans.factory.annotation.Value;

public class TimeUtil {
  private TimeUtil() {
    throw new IllegalStateException("TimeUtil class");
  }

  private static String zone;

  @Value("${server.timeZone}")
  public void setZoneStatic(String zone) {
    TimeUtil.zone = zone;
  }

  public static Instant instantNow() {
    return Instant.now().atZone(ZoneId.of(zone)).toInstant();
  }

  public static Date dateNow() {
    return Date.from(instantNow());
  }

  public static LocalDate localDateNow() {
    return instantNow().atZone(ZoneId.of(zone)).toLocalDate();
  }

  public static LocalDateTime localDateTimeNow() {
    return instantNow().atZone(ZoneId.of(zone)).toLocalDateTime();
  }

  public static LocalDate convertLocalDate(String date) {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(UtilConstants.PATTERN_LOCAL_DATE);
    try {
      return LocalDate.parse(date, dateFormat);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Formato de fecha y hora inválido: " + date);
    }
  }

  public static LocalDateTime convertLocalDateTime(String date) {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(UtilConstants.PATTERN_LOCAL_DATE);
    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(UtilConstants.PATTERN_DATE_TIME);
    try {
      return LocalDateTime.parse(date, dateTimeFormat);
    } catch (DateTimeParseException e) {
      try {
        return LocalDateTime.of(LocalDate.parse(date, dateFormat), LocalTime.MIN);
      } catch (DateTimeParseException ex) {
        throw new UnExpectedException("Formato de fecha y hora inválido: " + date);
      }
    }
  }

  public static LocalDate timestampToLocalDate(Timestamp timestamp) {
    return timestamp.toInstant().atZone(ZoneId.of(zone)).toLocalDateTime().toLocalDate();
  }

  public static LocalDateTime castToLocalDateTime(Timestamp timestamp) {
    return timestamp.toInstant().atZone(ZoneId.of(zone)).toLocalDateTime();
  }
}
