package dev.barrikeit.util.exceptions;

public class NoSuchMethodException extends BaseException {

  public NoSuchMethodException(String message) {
    super(message);
  }

  public NoSuchMethodException(String message, Object... messageArgs) {
    super(message, messageArgs);
  }
}
