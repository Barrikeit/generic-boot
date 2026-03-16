package dev.barrikeit.util.constants;

public class EntityConstants {
  private EntityConstants() {
    throw new IllegalStateException("Constants class");
  }

  public static final String DATE_COLUMN_DEFINITION = "TIMESTAMP WITH TIME ZONE";
  public static final String BPCHAR_COLUMN_DEFINITION = "CHAR(36)";

  // tables
  public static final String DIRECTIONS = "directions";
  public static final String LOCATIONS = "locations";
  public static final String MODULES = "modules";
  public static final String ROLES = "roles";
  public static final String USERS = "users";
  public static final String USER_SECURITY = "user_security";
  public static final String USER_SESSIONS = "user_sessions";

  // columns
  public static final String ID = "id";
  public static final String MAPS_ID = "owner";
  public static final String CODE = "code";

  // columns
  public static final String ID_DIRECTION = "id_direction";
  public static final String ID_LOCATION = "id_location";
  public static final String ID_USER = "id_user";
  public static final String NAME = "name";
  public static final String COUNTRY = "country";
  public static final String CITY = "city";
  public static final String STREET = "street";
  public static final String NUMBER = "number";
  public static final String POSTAL_CODE = "postal_code";
  public static final String EXTRA = "extra";
  public static final String USERNAME = "username";
  public static final String EMAIL = "email";
  public static final String PHONE = "phone";
  public static final String PASSWORD = "password";
  public static final String SURNAME1 = "surname1";
  public static final String SURNAME2 = "surname2";
  public static final String REGISTRATION_DATE = "registration_date";
  public static final String VERIFICATION_TOKEN = "verification_token";
  public static final String ENABLED = "enabled";
  public static final String LOGIN_ATTEMPTS = "login_attempts";
  public static final String LOGIN_DATE = "login_date";
  public static final String BANNED = "banned";
  public static final String BAN_DATE = "ban_date";
  public static final String BAN_REASON = "ban_reason";
  public static final String JTI = "jti";
  public static final String JTI_PAIR = "jti_pair";
  public static final String ISSUED_AT = "issued_at";
  public static final String EXPIRES_AT = "expires_at";
  public static final String TOKEN_TYPE = "token_type";
}