package dev.barrikeit.rest;

import dev.barrikeit.config.ApplicationProperties;
import dev.barrikeit.rest.base.Response;
import dev.barrikeit.service.dto.VersionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/version")
public class VersionController implements VersionApi {

  private final ApplicationProperties.GenericProperties genericProperties;

  @Value("${spring.profiles.active:${spring.profiles.default:unknown}}")
  private String environment;

  @Override
  @GetMapping
  public Response<VersionDto> getVersion() {
    return Response.ok(
        new VersionDto(
            genericProperties.getName(),
            genericProperties.getVersion(),
            genericProperties.getBuild(),
            environment));
  }
}
