package org.barrikeit.config.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.service.dto.JwtDto;
import org.barrikeit.config.security.service.dto.LoginDto;
import org.barrikeit.config.security.service.dto.ResponseDto;
import org.barrikeit.config.security.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    request.getSession().invalidate();
    return ResponseEntity.ok(new ResponseDto("Logout finalizado con éxito en el servidor"));
  }
}
