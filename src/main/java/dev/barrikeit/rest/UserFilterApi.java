package dev.barrikeit.rest;

import dev.barrikeit.rest.Response;
import dev.barrikeit.service.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Users (Search)", description = "Filter/search endpoints for users")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/users")
public interface UserFilterApi {

  @Operation(summary = "Search users with pagination and optional search text")
  @ApiResponse(
      responseCode = "200",
      description = "Page of users",
      content = @Content(schema = @Schema(implementation = Page.class)))
  @GetMapping
  Response<Page<UserDto>> findAllFiltered(
      @Parameter(hidden = true) @PageableDefault(size = 20) Pageable page,
      @Parameter(description = "Search query", required = false)
          @RequestParam(required = false, defaultValue = "")
          String search);
}
