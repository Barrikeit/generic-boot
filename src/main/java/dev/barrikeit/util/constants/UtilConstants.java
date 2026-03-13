package dev.barrikeit.util.constants;

public class UtilConstants {
  private UtilConstants() {
    throw new IllegalStateException("Constants class");
  }

  public static final String UNPAGED_PARAMETRO_BUSQUEDA = "unpaged";
  public static final String SEPARADOR_CAMPOS_BUSQUEDA = ";";
  public static final String EXPRESION_REGULAR_PARAMETROS =
      "(\\w+)([:!><])([^" + SEPARADOR_CAMPOS_BUSQUEDA + "]+)";
  public static final String PATTERN_LOCAL_DATE = "dd/MM/yyyy";
  public static final String PATTERN_DATE_TIME = "dd/MM/yyyy HH:mm:ss";
  public static final String PATTERN_DATE_TIME_MILLI = "dd/MM/yyyy HH:mm:ss.SSS";
  public static final String PATTERN_LOCAL_DATE_DOWNLOAD = "dd-MM-yyyy";
  public static final String PATTERN_DATE_TIME_DOWNLOAD = "dd-MM-yyyy_HHmmss";
}
