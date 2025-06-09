package org.barrikeit.config.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.service.AuthService;
import org.barrikeit.config.security.service.dto.JwtDto;
import org.barrikeit.config.security.service.dto.LoginDto;
import org.barrikeit.config.security.service.dto.LogoutDto;
import org.barrikeit.util.constants.JwtConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Validated
@RestController
@RequestMapping("/auth")
public class AuthController<S extends Session> {

  private final AuthService<S> authService;

  public AuthController(AuthService<S> authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<JwtDto> login(
      HttpServletRequest request, @RequestBody @Valid LoginDto loginDto) {
    return ResponseEntity.ok(authService.login(request, loginDto));
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(HttpServletRequest request) {
    return ResponseEntity.ok(authService.refresh(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<LogoutDto> logout(HttpServletRequest request) {
    authService.logout(request);
    return ResponseEntity.ok(new LogoutDto("Logout finalizado con éxito en el servidor"));
  }

  @PostMapping("/check")
  public ResponseEntity<JwtDto> checkSession(
      @CookieValue(name = JwtConstants.JWT_COOKIE_NAME, required = false) String cookie) {
    try {
      return ResponseEntity.ok(authService.checkLogin(cookie));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }
}
