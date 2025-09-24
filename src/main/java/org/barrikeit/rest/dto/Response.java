package org.barrikeit.rest.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.barrikeit.util.TimeUtil;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class Response<T> {

  private HttpStatus status;
  private Instant timestamp;
  private String message;
  private T content;

  public Response(HttpStatus status, String message, T content) {
    this.timestamp = TimeUtil.instantNow();
    this.status = status;
    this.message = message;
    this.content = content;
  }

  public static <T> Response<T> ok(T content) {
    return new Response<>(HttpStatus.OK, null, content);
  }

  public static <T> Response<T> ok(String message, T content) {
    return new Response<>(HttpStatus.OK, message, content);
  }

  public static <T> Response<T> error(HttpStatus status, String message) {
    return new Response<>(status, message, null);
  }
}
