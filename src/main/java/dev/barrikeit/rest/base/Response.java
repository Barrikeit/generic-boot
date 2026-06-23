package dev.barrikeit.rest.base;

import dev.barrikeit.util.TimeUtil;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class Response<T> {

  private HttpStatus status;
  private Instant timestamp;
  private String message;
  private String requestId;
  private Map<String, Object> meta;
  private T content;

  public Response(HttpStatus status, String message, T content) {
    this.timestamp = TimeUtil.instantNow();
    this.status = status;
    this.message = message;
    this.requestId = MDC.get("requestId");
    this.content = content;
  }

  public Response(HttpStatus status, String message, T content, Map<String, Object> meta) {
    this(status, message, content);
    this.meta = meta;
  }

  public static <T> Response<T> ok(T content) {
    return new Response<>(HttpStatus.OK, null, content);
  }

  public static <T> Response<T> ok(String message, T content) {
    return new Response<>(HttpStatus.OK, message, content);
  }

  public static <T> Response<T> ok(T content, Map<String, Object> meta) {
    return new Response<>(HttpStatus.OK, null, content, meta);
  }

  public static Response<Void> noContent(String message) {
    return new Response<>(HttpStatus.OK, message, null);
  }

  public static <T> Response<T> error(HttpStatus status, String message) {
    return new Response<>(status, message, null);
  }
}
