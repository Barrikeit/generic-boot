package dev.barrikeit.rest;

import dev.barrikeit.util.exceptions.BaseException;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Single point that translates every uncaught exception into a consistent RFC 9457 {@link
 * ProblemDetail} JSON response.
 *
 * <p>Every handler logs the exception at ERROR with cause, message, and full stack trace — this is
 * the single authoritative log entry for the exception. The {@code LoggingAspect} only traces
 * entry/exit at DEBUG and does not log exceptions.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionController {

  private static final Map<String, HttpStatus> STATUS_MAP =
      Map.ofEntries(
          Map.entry("BadRequestException", HttpStatus.BAD_REQUEST),
          Map.entry("UnauthorizedException", HttpStatus.UNAUTHORIZED),
          Map.entry("ForbiddenException", HttpStatus.FORBIDDEN),
          Map.entry("NotFoundException", HttpStatus.NOT_FOUND),
          Map.entry("ConflictException", HttpStatus.CONFLICT),
          Map.entry("GoneException", HttpStatus.GONE),
          Map.entry("TooManyRequestsException", HttpStatus.TOO_MANY_REQUESTS));

  private final MessageSource messageSource;

  @Autowired
  public ExceptionController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ProblemDetail> handleBaseException(BaseException ex, Locale locale) {
    logException("BaseException", ex);

    HttpStatus status = resolveStatus(ex);
    String title = resolveTitle(ex, locale);
    String detail = resolveDetail(ex, locale);

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setTitle(title);
    problem.setType(URI.create("urn:error:" + toKebab(ex.getClass().getSimpleName())));

    return ResponseEntity.status(status).body(problem);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthentication(
      AuthenticationException ex, Locale locale) {
    logException("AuthenticationException", ex);

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            resolveMessage("error.detail.unauthorized", locale, ex.getMessage()));
    problem.setTitle(resolveMessage("error.title.unauthorized", locale, "Unauthorized"));
    problem.setType(URI.create("urn:error:unauthorized"));

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex, Locale locale) {
    logException("AccessDeniedException", ex);

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            resolveMessage("error.detail.forbidden", locale, ex.getMessage()));
    problem.setTitle(resolveMessage("error.title.forbidden", locale, "Forbidden"));
    problem.setType(URI.create("urn:error:forbidden"));

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  public ResponseEntity<ProblemDetail> handleValidation(Exception ex, WebRequest request) {
    logException("ValidationException", ex);

    Locale locale = request.getLocale();
    List<Map<String, String>> fieldErrors = new ArrayList<>();

    if (ex instanceof MethodArgumentNotValidException manv) {
      manv.getBindingResult()
          .getFieldErrors()
          .forEach(
              fe -> {
                Map<String, String> entry = new LinkedHashMap<>();
                entry.put("field", fe.getField());
                entry.put("rejected", Objects.toString(fe.getRejectedValue(), ""));
                entry.put(
                    "message",
                    resolveMessage(fe.getDefaultMessage(), locale, fe.getDefaultMessage()));
                fieldErrors.add(entry);
              });
    } else if (ex instanceof ConstraintViolationException cve) {
      cve.getConstraintViolations()
          .forEach(
              cv -> {
                Map<String, String> entry = new LinkedHashMap<>();
                entry.put("field", cv.getPropertyPath().toString());
                entry.put("rejected", Objects.toString(cv.getInvalidValue(), ""));
                entry.put("message", resolveMessage(cv.getMessage(), locale, cv.getMessage()));
                fieldErrors.add(entry);
              });
    }

    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle(resolveMessage("error.title.validation", locale, "Validation Error"));
    problem.setType(URI.create("urn:error:validation"));
    problem.setProperty("errors", fieldErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(ErrorResponseException.class)
  public ResponseEntity<ProblemDetail> handleSpringError(ErrorResponseException ex, Locale locale) {
    logException("ErrorResponseException", ex);

    ProblemDetail problem = ex.getBody();
    problem.setTitle(resolveMessage(problem.getTitle(), locale, problem.getTitle()));
    problem.setDetail(
        resolveMessage(
            ex.getDetailMessageCode(),
            locale,
            ex.getBody().getDetail(),
            ex.getDetailMessageArguments()));

    return ResponseEntity.status(ex.getStatusCode()).body(problem);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, Locale locale) {
    logException("UnhandledException", ex);

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            resolveMessage("error.detail.internal", locale, "An unexpected error occurred"));
    problem.setTitle(resolveMessage("error.title.internal", locale, "Internal Server Error"));
    problem.setType(URI.create("urn:error:internal"));

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }

  /**
   * Single logging method — every handler delegates here. Produces one ERROR entry with cause,
   * message, and full stack trace.
   */
  private void logException(String handler, Throwable ex) {
    log.error(
        "{} with cause = '{}' and exception = '{}'",
        handler,
        ex.getCause() != null ? ex.getCause() : "NULL",
        ex.getMessage(),
        ex);
  }

  private HttpStatus resolveStatus(BaseException ex) {
    return STATUS_MAP.getOrDefault(ex.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String resolveTitle(BaseException ex, Locale locale) {
    String className = ex.getClass().getSimpleName();
    return resolveMessage("error.title." + className, locale, humanize(className));
  }

  /**
   * i18n key → MessageSource with args plain str → MessageFormat/String.format with args plain str
   * → as-is
   */
  private String resolveDetail(BaseException ex, Locale locale) {
    String raw = ex.getMessage();
    if (raw == null) return "No detail available";

    Object[] args = ex.getMessageArgs();

    if (ex.isI18nKey()) {
      try {
        return messageSource.getMessage(raw, args, locale);
      } catch (NoSuchMessageException e) {
        log.warn("Missing i18n key: {}", raw);
        return raw;
      }
    }

    if (args != null && args.length > 0) {
      return BaseException.formatMessage(raw, args);
    }

    return raw;
  }

  private String resolveMessage(String key, Locale locale, String fallback, Object... args) {
    if (key == null) return fallback;
    try {
      return messageSource.getMessage(key, args, locale);
    } catch (NoSuchMessageException e) {
      return fallback;
    }
  }

  private static String humanize(String className) {
    return className.replaceAll("Exception$", "").replaceAll("([a-z])([A-Z])", "$1 $2").trim();
  }

  private static String toKebab(String camelCase) {
    return camelCase.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
  }
}
