package dev.barrikeit.util.exceptions;

public class UnexpectedException extends BaseException {

  public UnexpectedException(String message) {
    super(message);
  }

  public UnexpectedException(String message, Object... messageArgs) {
    super(message, messageArgs);
  }
}
