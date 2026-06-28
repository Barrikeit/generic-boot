package dev.barrikeit.security.auth;

import dev.barrikeit.rest.Response;
import dev.barrikeit.security.rest.dto.JwtDto;
import dev.barrikeit.security.rest.dto.LoginDto;
import dev.barrikeit.security.rest.dto.RegisterDto;
import dev.barrikeit.security.util.JwtConstants;
import dev.barrikeit.util.constants.ExceptionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  @Override
  public Response<String> register(@RequestBody @Valid RegisterDto registerDto) {
    return Response.ok(authService.register(registerDto));
  }

  @PutMapping("/verify")
  @Override
  public Response<Void> verify(@RequestParam("t") String token) {
    authService.verify(token);
    return Response.noContent("Verification finalizado con éxito");
  }

  @PostMapping("/login")
  @Override
  public Response<JwtDto> login(@RequestBody @Valid LoginDto loginDto) {
    return Response.ok(authService.login(loginDto));
  }

  @PostMapping("/refresh")
  @Override
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
  @Override
  public Response<Void> logout(HttpServletRequest request) {
    String accessToken = extractToken(request);
    authService.logout(accessToken);
    return Response.noContent("Logout finalizado con éxito");
  }

  @PostMapping("/check")
  @Override
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
