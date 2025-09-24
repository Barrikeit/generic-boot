package org.barrikeit.config.security.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.rest.dto.JwtDto;
import org.barrikeit.config.security.rest.dto.LoginDto;
import org.barrikeit.config.security.rest.dto.RegisterDto;
import org.barrikeit.config.security.service.AuthService;
import org.barrikeit.rest.dto.Response;
import org.barrikeit.util.constants.JwtConstants;
import org.springframework.http.HttpStatus;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthController<S extends Session> {

  private final AuthService<S> authService;

  public AuthController(AuthService<S> authService) {
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
  public Response<JwtDto> login(HttpServletRequest request, @RequestBody @Valid LoginDto loginDto) {
    return Response.ok(authService.login(request, loginDto));
  }

  @PostMapping("/refresh")
  public Response<JwtDto> refresh(HttpServletRequest request) {
    return Response.ok(authService.refresh(request));
  }

  @PostMapping("/logout")
  public Response<Void> logout(HttpServletRequest request) {
    authService.logout(request);
    return Response.ok("Logout finalizado con éxito", null);
  }

  @PostMapping("/check")
  public Response<JwtDto> checkSession(
      @CookieValue(name = JwtConstants.JWT_COOKIE_NAME, required = false) String cookie) {
    try {
      return Response.ok(authService.checkLogin(cookie));
    } catch (Exception e) {
      return Response.error(HttpStatus.UNAUTHORIZED, "No autorizado");
    }
  }
}
