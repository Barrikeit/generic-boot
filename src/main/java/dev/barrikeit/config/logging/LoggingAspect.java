package dev.barrikeit.config.logging;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * Aspect for tracing method entry and exit on Spring components.
 *
 * <p>Logs method entry (with arguments) and exit (with result) at DEBUG level. Does <b>not</b> log
 * exceptions — that responsibility belongs to:
 *
 * <ul>
 *   <li>{@code ExceptionController} for request-thread exceptions
 *   <li>{@code LoggerConfig.installUncaughtExceptionHandler} for rogue threads
 *   <li>The developer explicitly, via {@code Exceptions.logged()}, at the throw site
 * </ul>
 */
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

  private final Environment env;

  @Pointcut(
      "within(@org.springframework.stereotype.Repository *)"
          + " || within(@org.springframework.stereotype.Service *)"
          + " || within(@org.springframework.web.bind.annotation.RestController *)")
  public void springBeanPointcut() {}

  @Pointcut(
      "within(dev.barrikeit.model.repository..*)"
          + " || within(dev.barrikeit.service..*)"
          + " || within(dev.barrikeit.rest..*)")
  public void applicationPackagePointcut() {}

  private Logger logger(JoinPoint joinPoint) {
    return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
  }

  @Around("applicationPackagePointcut() && springBeanPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    Logger log = logger(joinPoint);
    if (log.isDebugEnabled()) {
      log.debug(
          "Enter: {}() with argument[s] = {}",
          joinPoint.getSignature().getName(),
          Arrays.toString(joinPoint.getArgs()));
    }
    try {
      Object result = joinPoint.proceed();
      if (log.isDebugEnabled()) {
        log.debug("Exit: {}() with result = {}", joinPoint.getSignature().getName(), result);
      }
      return result;
    } catch (Throwable e) {
      if (log.isDebugEnabled()) {
        log.debug(
            "Exit: {}() with exception = {}",
            joinPoint.getSignature().getName(),
            e.getClass().getSimpleName());
      }
      throw e;
    }
  }
}
