package dev.barrikeit.util.exceptions;

import dev.barrikeit.exception.BaseException;

public class NoSuchMethodException extends BaseException {

  public NoSuchMethodException(String message) {
    super(message);
  }

  public NoSuchMethodException(String message, Object... messageArgs) {
    super(message, messageArgs);
  }
}
