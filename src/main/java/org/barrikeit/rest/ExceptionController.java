package org.barrikeit.rest;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.util.exceptions.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Log4j2
@RestControllerAdvice
public class ExceptionController {

  private final MessageSource messageSource;

  @Autowired
  public ExceptionController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /** Handle custom application exceptions (GenericException). */
  @ExceptionHandler(GenericException.class)
  public ResponseEntity<ProblemDetail> handleGenericException(GenericException ex, Locale locale) {
    log.error("GenericException: {}", ex.getMessage(), ex);

    ProblemDetail problem = ex.getBody();

    String resolvedTitle =
        resolveMessageOrKey(ex.getTitleMessageCode(), locale, ex.getDetailMessageArguments());
    String resolvedDetail =
        resolveMessageOrKey(ex.getDetailMessageCode(), locale, ex.getDetailMessageArguments());

    problem.setTitle(resolvedTitle);
    problem.setDetail(resolvedDetail);

    return ResponseEntity.status(ex.getStatusCode()).body(problem);
  }

  /** Handle Spring framework's own ErrorResponseException. */
  @ExceptionHandler(ErrorResponseException.class)
  public ResponseEntity<ProblemDetail> handleSpringErrorResponseException(
      ErrorResponseException ex, Locale locale) {
    log.error("Spring ErrorResponseException: {}", ex.getMessage(), ex);

    ProblemDetail problem = ex.getBody();

    String resolvedTitle =
        resolveMessageOrKey(problem.getTitle(), locale, ex.getDetailMessageArguments());
    String resolvedDetail =
        resolveMessageOrKey(ex.getDetailMessageCode(), locale, ex.getDetailMessageArguments());

    problem.setTitle(resolvedTitle);
    problem.setDetail(resolvedDetail);

    return ResponseEntity.status(ex.getStatusCode()).body(problem);
  }

  /**
   * Handle validation errors:
   *
   * <ul>
   *   <li>MethodArgumentNotValidException (invalid request body)
   *   <li>ConstraintViolationException (invalid query/path params)
   * </ul>
   */
  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  public ResponseEntity<Object> handleValidationExceptions(Exception ex, WebRequest request) {
    log.error("Validation error: {}", ex.getMessage(), ex);

    HttpStatus status = HttpStatus.BAD_REQUEST;
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("type", "validation-exception");
    body.put("status", status.value());
    body.put("title", resolveMessageOrKey("ERROR_NOT_CONTROLLED", request.getLocale()));

    List<Map<String, String>> errors = new ArrayList<>();

    if (ex instanceof MethodArgumentNotValidException manv) {
      manv.getBindingResult()
          .getFieldErrors()
          .forEach(
              error -> {
                Map<String, String> err = new HashMap<>();
                err.put("field", error.getField());
                err.put("rejectedValue", Objects.toString(error.getRejectedValue(), ""));
                err.put(
                    "message",
                    resolveMessageOrKey(
                        error.getDefaultMessage(), request.getLocale(), new Object[] {}));
                errors.add(err);
              });
    } else if (ex instanceof ConstraintViolationException cve) {
      cve.getConstraintViolations()
          .forEach(
              v -> {
                Map<String, String> violation = new HashMap<>();
                violation.put("property", v.getPropertyPath().toString());
                violation.put("invalidValue", Objects.toString(v.getInvalidValue(), ""));
                violation.put(
                    "message",
                    resolveMessageOrKey(v.getMessage(), request.getLocale(), new Object[] {}));
                errors.add(violation);
              });
    }

    body.put("errors", errors);
    return ResponseEntity.status(status).body(body);
  }

  /** Catch-all handler for unexpected/uncontrolled exceptions. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleAllOtherExceptions(Exception ex, Locale locale) {
    log.error("Unhandled exception: {}", ex.getMessage(), ex);

    String fallback = messageSource.getMessage("ERROR_NOT_CONTROLLED", null, locale);

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, fallback);
    problem.setType(URI.create(""));
    problem.setTitle(fallback);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }

  /** Resolve message code from bundles, fallback to key itself if missing. */
  private String resolveMessageOrKey(String key, Locale locale, Object... args) {
    if (key == null) return null;
    try {
      return messageSource.getMessage(key, args, locale);
    } catch (NoSuchMessageException e) {
      log.debug("No translation found for key '{}'", key);
      return key;
    }
  }
}
