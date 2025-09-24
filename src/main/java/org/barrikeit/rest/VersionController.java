package org.barrikeit.rest;

import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.ApplicationProperties;
import org.barrikeit.rest.dto.Response;
import org.barrikeit.rest.dto.VersionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/version")
public class VersionController {
  private final ApplicationProperties.GenericProperties genericProperties;

  @Value("${spring.profiles.active:unknown}")
  private String environment;

  public VersionController(ApplicationProperties.GenericProperties genericProperties) {
    this.genericProperties = genericProperties;
  }

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
