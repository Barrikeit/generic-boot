package dev.barrikeit.security.util;

public class JwtConstants {
  private JwtConstants() {
    throw new IllegalStateException("Constants class");
  }

  public static final String JWT = "access-token";
  public static final String JWT_REFRESH = "refresh-token";
  public static final String USER = "user";
  public static final String REFRESHABLE = "refreshable";
  public static final String ROLES = "roles";
  public static final String AUTHORITIES = "authorities";
}
