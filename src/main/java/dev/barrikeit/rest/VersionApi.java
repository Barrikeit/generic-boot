package dev.barrikeit.rest;

import dev.barrikeit.rest.Response;
import dev.barrikeit.service.dto.VersionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Version", description = "Application version endpoint")
@RequestMapping("/version")
public interface VersionApi {

  @Operation(summary = "Get application version")
  @ApiResponse(
      responseCode = "200",
      description = "Version info",
      content = @Content(schema = @Schema(implementation = VersionDto.class)))
  @GetMapping
  Response<VersionDto> getVersion();
}
