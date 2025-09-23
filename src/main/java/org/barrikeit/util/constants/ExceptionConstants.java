package org.barrikeit.util.constants;

public class ExceptionConstants {
  private ExceptionConstants() {
    throw new IllegalStateException("Constants class");
  }

  //TODO: reemplazar textos de las constantes por variables usando el LocaleConfiguration y el MessageSource

  // Titulos para los constructores de las excepciones
  public static final String BAD_REQUEST = "The received request has an incorrect format";
  public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  public static final String NOT_FOUND = "Not found Exception";

  // Mensages para las nuevas instancias de las Excepciones que extiendan de GenericException()
  public static final String ERROR_INTERNAL_SERVER =
      "An internal error has occurred. Please contact the administrator.";
  public static final String ERROR_NOT_FOUND = "Entity not found, {0}";
  public static final String ERROR_PARAMS_VALIDATION = "Invalid parameters";
  public static final String ERROR_FIELD_GET_VALUE =
      "Error al obtener el valor del campo {0} de la clase {1}.";
  public static final String ERROR_FIELD_SET_VALUE =
      "Error al insertar el valor al campo {0} de la clase {1}.";
  public static final String ERROR_MISSING_ANNOTATION =
      "No existe la anotación {0} en la clase : {1}";

  // Tokens
  public static final String ERROR_TOKEN_NOT_PRESENT = "";
  public static final String ERROR_TOKEN_INVALID = "";
  public static final String ERROR_TOKEN_EXPIRED = "";
  public static final String ERROR_TOKEN_MISMATCH = "";
  public static final String ERROR_TOKEN_ALREDY_USED = "";
  public static final String EMPTY_COOKIE = "";
  public static final String DESERIALIZED_COOKIE = "";

  // User
  public static final String ERROR_REQUEST_MUST_NOT_BE_NULL = "";
  public static final String ERROR_MAX_SESSIONS_CONCURRENT_USER = "";
  public static final String ERROR_USER_BANNED = "";
  public static final String ERROR_USER_NOT_ENABLED = "";
  public static final String ERROR_USER_NAME_ALREADY_EXISTS = "";
  public static final String ERROR_USER_EMAIL_ALREADY_EXISTS = "";
  public static final String ERROR_USER_DEACTIVATE_HIMSELF = "";
  public static final String ERROR_USER_ALREADY_ENABLED = "";
}
