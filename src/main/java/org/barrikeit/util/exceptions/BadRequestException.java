package org.barrikeit.util.exceptions;

import java.net.URI;
import org.barrikeit.util.constants.ExceptionConstants;
import org.springframework.http.HttpStatus;

public class BadRequestException extends GenericException {

  static final URI TYPE = URI.create("");

  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

  public BadRequestException(String message, Object... messageArgs) {
    super(HttpStatus.BAD_REQUEST, TYPE, ExceptionConstants.BAD_REQUEST, message, messageArgs);
  }
}
