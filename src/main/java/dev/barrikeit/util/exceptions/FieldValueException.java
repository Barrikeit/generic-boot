package dev.barrikeit.util.exceptions;

public class FieldValueException extends BaseException {

  public FieldValueException(String message, Object... messageArgs) {
    super(message, messageArgs);
  }
}
