package dev.barrikeit.rest;

import dev.barrikeit.rest.Response;
import dev.barrikeit.service.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Users (CRUD)", description = "CRUD related endpoints for users")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/users")
public interface UserCrudApi {

  @Operation(summary = "List users", description = "Disabled in this API")
  @ApiResponse(responseCode = "403", description = "Endpoint disabled")
  @GetMapping(params = {"!page", "!size", "!sort", "!search"})
  Response<List<UserDto>> findAll();

  @Operation(summary = "Find user by id")
  @ApiResponse(
      responseCode = "200",
      description = "Found",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  @GetMapping("/id/{id}")
  Response<UserDto> findById(@PathVariable("id") UUID id);

  @Operation(summary = "Find user by username")
  @ApiResponse(
      responseCode = "200",
      description = "Found",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  @GetMapping("/{username}")
  Response<UserDto> findByUsername(@PathVariable("username") String username);
}
