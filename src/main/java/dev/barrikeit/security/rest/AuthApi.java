package dev.barrikeit.security.rest;

import dev.barrikeit.rest.base.Response;
import dev.barrikeit.security.rest.dto.JwtDto;
import dev.barrikeit.security.rest.dto.LoginDto;
import dev.barrikeit.security.rest.dto.RegisterDto;
import dev.barrikeit.security.util.JwtConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Endpoints for user authentication and session management")
@RequestMapping("/auth")
public interface AuthApi {
  @Operation(summary = "Register a new user")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Registered successfully",
          content = @Content(schema = @Schema(implementation = String.class)))
  })
  @PostMapping("/register")
  Response<String> register(@RequestBody @Valid RegisterDto registerDto);

  @Operation(summary = "Verify email using token")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Verified successfully")
  })
  @PutMapping("/verify")
  Response<Void> verify(@RequestParam("t") String token);

  @Operation(summary = "Login and obtain JWT tokens")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Authenticated",
          content = @Content(schema = @Schema(implementation = JwtDto.class)))
  })
  @PostMapping("/login")
  Response<JwtDto> login(@RequestBody @Valid LoginDto loginDto);

  @Operation(summary = "Refresh access token using refresh token", security = {@SecurityRequirement(name = "bearerAuth")})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Refreshed",
          content = @Content(schema = @Schema(implementation = JwtDto.class)))
  })
  @PostMapping("/refresh")
  Response<JwtDto> refresh(
      HttpServletRequest request,
      @Parameter(description = "Refresh token", required = true)
      @RequestHeader(name = JwtConstants.JWT_REFRESH) String refreshToken);

  @Operation(summary = "Logout current session", security = {@SecurityRequirement(name = "bearerAuth")})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Logged out")
  })
  @PostMapping("/logout")
  Response<Void> logout(HttpServletRequest request);

  @Operation(summary = "Check current session and return user info", security = {@SecurityRequirement(name = "bearerAuth")})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Active session",
          content = @Content(schema = @Schema(implementation = JwtDto.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  @PostMapping("/check")
  Response<JwtDto> checkSession(HttpServletRequest request);
}
