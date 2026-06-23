package dev.barrikeit.rest;

import dev.barrikeit.web.filter.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Application-specific exception handling that takes precedence over the core {@link
 * dev.barrikeit.rest.ExceptionController}.
 *
 * <p>This advice only covers what core does not (or handles differently); every other exception
 * falls through to the core handler. Add app-specific handlers here as needed.
 *
 * <ul>
 *   <li>{@code ErrorResponseException} — the app's {@link
 *       dev.barrikeit.util.exceptions.GenericException} family carries its own status/{@code
 *       ProblemDetail}; without this it would be swallowed by core's catch-all 500 handler.
 *   <li>{@code ConstraintViolationException} — bean validation on {@code @Validated} params (core
 *       only handles {@code MethodArgumentNotValidException}).
 * </ul>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class AppExceptionController {

  @ExceptionHandler(ErrorResponseException.class)
  public ProblemDetail handleErrorResponse(ErrorResponseException ex, HttpServletRequest req) {
    log.debug("ErrorResponseException: {}", ex.getMessage());
    ProblemDetail problem = ex.getBody();
    problem.setInstance(URI.create(req.getRequestURI()));
    problem.setProperty("correlationId", MDC.get(CorrelationIdFilter.MDC_KEY));
    return problem;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest req) {
    log.debug("ConstraintViolationException: {}", ex.getMessage());
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setType(URI.create("urn:error:validation"));
    problem.setTitle("Validation failed");
    problem.setInstance(URI.create(req.getRequestURI()));
    problem.setProperty("correlationId", MDC.get(CorrelationIdFilter.MDC_KEY));
    problem.setProperty(
        "errors",
        ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .toList());
    return problem;
  }
}
