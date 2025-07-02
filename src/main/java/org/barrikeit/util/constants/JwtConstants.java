package org.barrikeit.util.constants;

public class JwtConstants {
  private JwtConstants() {
    throw new IllegalStateException("Constants class");
  }

  public static final String SCOPES = "scopes";
  public static final String DOMAIN = "domain";
  public static final String REFRESH = "refresh";
  public static final String ROLES = "roles";
  public static final String MODULES = "modules";
  public static final String SESSION_ID = "sessionId";
  public static final String JWT_COOKIE_NAME = "AUTH-JWT";
}
