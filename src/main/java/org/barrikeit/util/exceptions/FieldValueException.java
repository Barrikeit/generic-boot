package org.barrikeit.util.exceptions;

import java.net.URI;
import org.barrikeit.util.constants.ExceptionConstants;
import org.springframework.http.HttpStatus;

public class FieldValueException extends GenericException {

  static final URI TYPE = URI.create("");

  public FieldValueException(String message, Object... messageArgs) {
    super(
        HttpStatus.BAD_REQUEST,
        TYPE,
        ExceptionConstants.BAD_REQUEST,
        message,
        messageArgs);
  }
}
