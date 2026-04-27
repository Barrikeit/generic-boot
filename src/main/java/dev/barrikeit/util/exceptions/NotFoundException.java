package dev.barrikeit.util.exceptions;

public class NotFoundException extends BaseException {

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Object... messageArgs) {
    super(message, messageArgs);
  }
}
