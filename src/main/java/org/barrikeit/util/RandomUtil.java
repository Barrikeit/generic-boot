package org.barrikeit.util;

import java.util.Base64;
import java.util.Random;

public class RandomUtil {
  private RandomUtil() {
    throw new IllegalStateException("RandomUtil class");
  }

  private static final Random RANDOM = new Random();

  public static String getRandomBase64EncodedString(int length) {
    byte[] responseHeader = new byte[length];
    RANDOM.nextBytes(responseHeader);
    return Base64.getEncoder().encodeToString(responseHeader);
  }
}
