package org.barrikeit.util.exceptions;

import java.io.Serial;
import java.net.URI;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.util.constants.ExceptionConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.util.ObjectUtils;
import org.springframework.web.ErrorResponseException;

@Log4j2
public class GenericException extends ErrorResponseException {
  @Serial private static final long serialVersionUID = 1L;

  public GenericException(HttpStatus status, String message) {
    super(status, ProblemDetail.forStatusAndDetail(status, message), new Throwable());
  }

  public GenericException(
      HttpStatus status, URI type, String title, String message, Object... messageArgs) {
    super(status, ProblemDetail.forStatus(status), new Throwable(), message, messageArgs);
    this.setType(type);
    this.setTitle(title);
  }
}
