package org.barrikeit.util.constants;

public class EntityConstants {
  private EntityConstants() {
    throw new IllegalStateException("Constants class");
  }

  public static final String DATE_COLUMN_DEFINITION = "TIMESTAMP WITH TIME ZONE";

  // tables
  public static final String LOCATIONS = "locations";
  public static final String MODULES = "modules";
  public static final String ROLES = "roles";
  public static final String USERS = "users";
  public static final String USER_SESSIONS = "user_sessions";
  // ids
  public static final String ID = "id";
  public static final String ID_LOCATION = "id_location";
  public static final String ID_MODULE = "id_module";
  public static final String ID_ROLE = "id_role";
  public static final String ID_USER = "id_user";
  public static final String ID_USER_SESSION = "id_user_session";
  // codes
  public static final String CODE = "code";
  public static final String CODE_LOCATION = "code_location";
  public static final String CODE_MODULE = "code_module";
  public static final String CODE_ROLE = "code_role";
  public static final String CODE_USER = "code_user";

  // columns
}
