package dev.barrikeit.security.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import dev.barrikeit.util.exceptions.BaseException;

public class HashEncodeUtil {
    private HashEncodeUtil() {
        throw new IllegalStateException("HashEncodeUtil class");
    }

  /**
   * Generates a SHA-256 hash of the provided secret and Base64-encodes it. Useful for storing a
   * deterministic secret in config files.
   *
   * @param secret the passphrase/secret string
   * @return Base64-encoded SHA-256 hash
   */
  public static String generateBase64Secret(String secret) {
    return Base64.getEncoder().encodeToString(sha256Bytes(secret));
  }

  /** Returns raw SHA-256 hash bytes */
  public static byte[] sha256Bytes(String secret) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
      throw new BaseException("SHA-256 algorithm not found", e);
    }
  }
}
