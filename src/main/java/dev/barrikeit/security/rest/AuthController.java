package dev.barrikeit.security.rest;

import dev.barrikeit.security.rest.dto.JwtDto;
import dev.barrikeit.security.rest.dto.LoginDto;
import dev.barrikeit.security.rest.dto.RegisterDto;
import dev.barrikeit.security.service.AuthService;
import dev.barrikeit.security.util.JwtConstants;
import dev.barrikeit.rest.base.Response;
import dev.barrikeit.util.constants.ExceptionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public Response<String> register(@RequestBody @Valid RegisterDto registerDto) {
    return Response.ok(authService.register(registerDto));
  }

  @PutMapping("/verify")
  public Response<String> verify(@RequestParam("t") String token) {
    authService.verify(token);
    return Response.ok("Verification finalizado con éxito", null);
  }

  @PostMapping("/login")
  public Response<JwtDto> login(@RequestBody @Valid LoginDto loginDto) {
    return Response.ok(authService.login(loginDto));
  }

  @PostMapping("/refresh")
  public Response<JwtDto> refresh(
      HttpServletRequest request,
      @RequestHeader(name = JwtConstants.JWT_REFRESH) String refreshToken) {
    String accessToken = extractToken(request);
    if (!StringUtils.hasText(refreshToken)) {
      throw new PreAuthenticatedCredentialsNotFoundException(
          ExceptionConstants.ERROR_TOKEN_NOT_PRESENT);
    }
    return Response.ok(authService.refresh(accessToken, refreshToken));
  }

  @PostMapping("/logout")
  public Response<Void> logout(HttpServletRequest request) {
    String accessToken = extractToken(request);
    authService.logout(accessToken);
    return Response.ok("Logout finalizado con éxito", null);
  }

  @PostMapping("/check")
  public Response<JwtDto> checkSession(HttpServletRequest request) {
    try {
      String accessToken = extractToken(request);
      return Response.ok(authService.checkSession(accessToken));
    } catch (Exception e) {
      return Response.error(HttpStatus.UNAUTHORIZED, "No autorizado");
    }
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
      throw new PreAuthenticatedCredentialsNotFoundException(
          ExceptionConstants.ERROR_TOKEN_NOT_PRESENT);
    }
    return header.substring(7);
  }
}
