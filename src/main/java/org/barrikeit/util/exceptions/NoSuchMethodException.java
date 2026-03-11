package org.barrikeit.util.exceptions;

import java.net.URI;
import org.barrikeit.util.constants.ExceptionConstants;
import org.springframework.http.HttpStatus;

public class NoSuchMethodException extends GenericException {

  static final URI TYPE = URI.create("");

  public NoSuchMethodException(String message) {
    super(HttpStatus.NOT_IMPLEMENTED, message);
  }

  public NoSuchMethodException(String message, Object... messageArgs) {
    super(
        HttpStatus.NOT_IMPLEMENTED, TYPE, ExceptionConstants.NO_SUCH_MERTHOD, message, messageArgs);
  }
}
