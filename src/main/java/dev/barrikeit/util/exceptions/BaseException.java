package dev.barrikeit.util.exceptions;

import java.io.Serial;

public class BaseException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public BaseException(String message) {
    super(message);
  }

  public BaseException(String message, Object... values) {
    super(String.format(message, values));
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
