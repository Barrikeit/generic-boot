package dev.barrikeit.util.exceptions;

public class BadRequestException extends BaseException {

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Object... messageArgs) {
    super(message, messageArgs);
  }
}
