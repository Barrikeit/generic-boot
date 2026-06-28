package dev.barrikeit.rest;

import dev.barrikeit.rest.Response;
import dev.barrikeit.service.dto.RoleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Roles", description = "Role lookup endpoints")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/roles")
public interface RoleApi {

  @Operation(summary = "List roles", description = "Disabled in this API")
  @ApiResponse(responseCode = "403", description = "Endpoint disabled")
  @GetMapping(params = {"!page", "!size", "!sort", "!search"})
  Response<List<RoleDto>> findAll();

  @Operation(summary = "Find role by id", description = "Disabled in this API")
  @ApiResponse(responseCode = "403", description = "Endpoint disabled")
  @GetMapping("/id/{id}")
  Response<RoleDto> findById(@PathVariable("id") Long id);

  @Operation(summary = "Find role by code")
  @ApiResponse(
      responseCode = "200",
      description = "Found",
      content = @Content(schema = @Schema(implementation = RoleDto.class)))
  @GetMapping("/code/{code}")
  Response<RoleDto> findByCode(@PathVariable("code") String code);
}
