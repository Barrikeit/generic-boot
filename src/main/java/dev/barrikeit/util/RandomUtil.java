package dev.barrikeit.util;

import java.util.Base64;
import java.util.Random;

public class RandomUtil {
  private static final Random RANDOM = new Random();

  private RandomUtil() {
    throw new IllegalStateException("RandomUtil class");
  }

  public static String getRandomBase64EncodedString(int length) {
    byte[] responseHeader = new byte[length];
    RANDOM.nextBytes(responseHeader);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(responseHeader);
  }
}
