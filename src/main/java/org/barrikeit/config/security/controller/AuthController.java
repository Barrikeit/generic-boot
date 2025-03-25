package org.barrikeit.config.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.service.AuthService;
import org.barrikeit.config.security.service.dto.JwtDto;
import org.barrikeit.config.security.service.dto.LoginDto;
import org.barrikeit.config.security.service.dto.ResponseDto;
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
      @RequestBody @Valid LoginDto loginDto, HttpServletRequest request) {
    return ResponseEntity.ok(authService.login(loginDto, request));
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(HttpServletRequest request) {
    return ResponseEntity.ok(authService.refresh(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<ResponseDto> logout(HttpServletRequest request) {
    authService.logout(request);
    return ResponseEntity.ok(new ResponseDto("Logout finalizado con éxito en el servidor"));
  }

  @PostMapping("/check")
  public ResponseEntity<JwtDto> checkSession(
      @CookieValue(name = JwtConstants.JWT_COOKIE_NAME, required = false) String cookie,
      HttpServletResponse response) {
    try {
      return ResponseEntity.ok(authService.checkLogin(cookie, response));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }
}
