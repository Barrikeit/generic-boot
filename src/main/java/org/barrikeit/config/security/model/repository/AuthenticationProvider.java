package org.barrikeit.config.security.model.repository;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.service.BasicUserDetailsService;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.NotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class AuthenticationProvider implements AuthRepository {

  private final BasicUserDetailsService basicUserDetailsService;

  private final PasswordEncoder passwordEncoder =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    UserDetails userDetails =
        retrieveUser(
            authentication.getName(), (UsernamePasswordAuthenticationToken) authentication);

    return new UsernamePasswordAuthenticationToken(
        userDetails, authentication.getCredentials().toString(), new ArrayList<>());
  }

  private UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    String presentedPassword = authentication.getCredentials().toString();
    UserDetails loadedUser = basicUserDetailsService.loadUser(username);

    if (loadedUser == null
        || !this.passwordEncoder.matches(presentedPassword, loadedUser.getPassword())) {
      log.info("Usuario {} no encontrado", username);
      throw new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, username);
    }
    return loadedUser;
  }
}
