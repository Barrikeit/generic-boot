package dev.barrikeit.security.service;

import dev.barrikeit.model.domain.Module;
import dev.barrikeit.model.domain.Role;
import dev.barrikeit.model.domain.User;
import dev.barrikeit.model.repository.RoleRepository;
import dev.barrikeit.model.repository.UserRepository;
import dev.barrikeit.security.model.domain.BasicUserDetails;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.mapper.UserMapper;
import dev.barrikeit.util.EmailUtil;
import dev.barrikeit.util.RandomUtil;
import dev.barrikeit.util.TimeUtil;
import dev.barrikeit.util.constants.ExceptionConstants;
import dev.barrikeit.util.enums.EmailType;
import dev.barrikeit.util.exceptions.BadRequestException;
import dev.barrikeit.util.exceptions.NotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class BasicUserDetailsService {

  private final UserRepository repository;
  private final UserMapper mapper;

  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public User findByUsername(final String username) throws NotFoundException {
    return repository
        .findByUsernameEqualsIgnoreCase(username)
        .orElseThrow(
            () -> {
              log.info("Usuario {} no encontrado", username);
              return new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, username);
            });
  }

  public BasicUserDetails loadUser(final String username) throws NotFoundException {
    User user = findByUsername(username);
    return new BasicUserDetails(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        user.isBanned(),
        getRoles(user),
        getAuthorities(user));
  }

  public BasicUserDetails loadUserByCode(UUID userCode) {
    User user =
        repository
            .findByCode(userCode)
            .orElseThrow(() -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, userCode));
    return new BasicUserDetails(
        user.getCode(),
        user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        user.isBanned(),
        getRoles(user),
        getAuthorities(user));
  }

  public UsernamePasswordAuthenticationToken authenticate(final UserDto dto)
      throws AuthenticationException {
    try {
      String username = dto.getUsername();
      String password = dto.getPassword();
      User user = findByUsername(username);
      if (!this.passwordEncoder.matches(password, user.getPassword())) {
        checkAttempts(user);
        throw new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, username);
      }
      return new UsernamePasswordAuthenticationToken(
          new BasicUserDetails(
              user.getCode(),
              user.getUsername(),
              user.getPassword(),
              user.isEnabled(),
              user.isBanned(),
              getRoles(user),
              getAuthorities(user)),
          dto.getPassword(),
          new ArrayList<>());
    } catch (NotFoundException e) {
      throw new BadCredentialsException("Bad Credentials");
    }
  }

  public String register(final UserDto dto) {
    User user = validateUserToCreateUpdate(dto, true);
    generateUserForCreateUpdate(dto, user);
    user.setUsername(dto.getUsername());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    mapper.updateEntity(dto, user);
    user.setRegistrationDate(TimeUtil.localDateTimeNow());
    user.setVerificationToken(RandomUtil.getRandomBase64EncodedString(14));

    user = repository.save(user);
    UserDto registeredUserDto = mapper.toDto(user);
    EmailUtil.sendEmail(registeredUserDto, EmailType.REGISTER_USER);
    return URLEncoder.encode(registeredUserDto.getVerificationToken(), StandardCharsets.UTF_8);
  }

  private User validateUserToCreateUpdate(UserDto dto, boolean isCreate) {
    User user = validateUsername(dto, isCreate);
    validateEmail(dto);
    return user;
  }

  private User validateUsername(UserDto dto, boolean isCreate) {
    User user = repository.findByUsernameEqualsIgnoreCase(dto.getUsername()).orElse(new User());
    if (isCreate && user.isNew()) {
      user.setCode(UUID.randomUUID());
    } else if (isCreate && !user.isNew()) {
      throw new BadRequestException(
          ExceptionConstants.ERROR_USER_NAME_ALREADY_EXISTS, dto.getUsername());
    } else if (!isCreate && user.isNew()) {
      throw new BadRequestException(ExceptionConstants.ERROR_NOT_FOUND, dto.getUsername());
    }
    return user;
  }

  private void validateEmail(UserDto dto) {
    if (repository.findByEmailEqualsIgnoreCase(dto.getEmail()).isPresent()) {
      throw new BadRequestException(
          ExceptionConstants.ERROR_USER_EMAIL_ALREADY_EXISTS, dto.getEmail());
    }
  }

  private void generateUserForCreateUpdate(UserDto dto, User user) {
    validateRoles(dto, user);
    // añadir otras validaciones si fuera necesario
  }

  private void validateRoles(UserDto dto, User user) {
    if (!dto.getRoles().isEmpty()) {
      Set<Role> roles =
          dto.getRoles().stream()
              .map(
                  role ->
                      roleRepository
                          .findByCode(role.getCode())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, role)))
              .collect(Collectors.toSet());
      user.getRoles().clear();
      user.getRoles().addAll(roles);
    }
  }

  public void verify(final String verificationToken) {
    User registeredUser =
        repository
            .findByVerificationToken(verificationToken)
            .orElseThrow(
                () -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, verificationToken));
    if (registeredUser.isEnabled()) {
      throw new BadRequestException(ExceptionConstants.ERROR_USER_ALREADY_ENABLED);
    } else {
      registeredUser.setVerificationToken(null);
      registeredUser.setEnabled(true);
      repository.save(registeredUser);
    }
  }

  public void checkAttempts(final User user) {
    int loginAttempts = user.getLoginAttempts();
    if (loginAttempts < 10) {
      loginAttempts++;
      user.setLoginAttempts(loginAttempts);
    } else {
      banUser(user);
    }
    repository.save(user);
  }

  public User banUser(final User user) {
    user.setBanned(Boolean.TRUE);
    user.setBanDate(TimeUtil.localDateTimeNow());
    return repository.save(user);
  }

  public User unbanUser(final User user) {
    user.setBanned(Boolean.FALSE);
    user.setBanDate(null);
    return repository.save(user);
  }

  public User updateLoginDateAndResetAttempts(final User user) {
    return repository.save(resetAttempts(updateLoginDate(user)));
  }

  private User resetAttempts(final User user) {
    user.setBanned(false);
    user.setBanDate(null);
    user.setLoginAttempts(0);
    return user;
  }

  private User updateLoginDate(final User user) {
    user.setLoginDate(TimeUtil.localDateTimeNow());
    return user;
  }

  private List<? extends GrantedAuthority> getRoles(User user) {
    List<GrantedAuthority> roles = new ArrayList<>();
    if (user.getRoles() != null) {
      for (Role role : user.getRoles().stream().toList()) {
        roles.add(new SimpleGrantedAuthority((role.getCode())));
      }
    } else {
      return Collections.emptyList();
    }
    return roles;
  }

  private List<? extends GrantedAuthority> getAuthorities(User user) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    if (user.getRoles() != null) {
      for (Role role : user.getRoles().stream().toList()) {
        for (Module modulo : role.getModules().stream().toList()) {
          authorities.add(new SimpleGrantedAuthority(role.getCode() + "_" + modulo.getCode()));
        }
      }
    } else {
      return Collections.emptyList();
    }
    return authorities;
  }
}
