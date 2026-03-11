package org.barrikeit.util;

import org.barrikeit.service.dto.UserDto;
import org.barrikeit.util.enums.EmailType;

public class EmailUtil {
  private EmailUtil() {
    throw new IllegalStateException("EmailUtil class");
  }

  public static void sendEmail(UserDto user, EmailType emailType) {}

  public static void sendEmail(UserDto user, EmailType emailType, String subject, String body) {}
}
