package org.barrikeit.util.exceptions;

import java.net.URI;
import org.barrikeit.util.constants.ExceptionConstants;
import org.springframework.http.HttpStatus;

public class UnExpectedException extends GenericException {

  static final URI TYPE = URI.create("");

  public UnExpectedException(String message) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }

  public UnExpectedException(String message, Object... messageArgs) {
    super(
        HttpStatus.INTERNAL_SERVER_ERROR,
        TYPE,
        ExceptionConstants.INTERNAL_SERVER_ERROR,
        message,
        messageArgs);
  }
}
