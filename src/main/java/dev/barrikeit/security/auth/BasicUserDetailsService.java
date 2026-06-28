package dev.barrikeit.security.auth;

import dev.barrikeit.exception.BadRequestException;
import dev.barrikeit.exception.NotFoundException;
import dev.barrikeit.exception.UnauthorizedException;
import dev.barrikeit.model.domain.Module;
import dev.barrikeit.model.domain.Role;
import dev.barrikeit.model.domain.User;
import dev.barrikeit.model.repository.RoleRepository;
import dev.barrikeit.model.repository.UserRepository;
import dev.barrikeit.security.data.entity.BasicUserDetails;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.mapper.UserMapper;
import dev.barrikeit.util.EmailUtil;
import dev.barrikeit.util.RandomUtil;
import dev.barrikeit.util.TimeUtil;
import dev.barrikeit.util.constants.ExceptionConstants;
import dev.barrikeit.util.enums.EmailType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

/**
 * Application {@link dev.barrikeit.security.service.BasicUserDetailsService} — loads users from the application database and maps
 * them to security {@link BasicUserDetails}.
 *
 * <p>Implements the core contract ({@code loadDetails}/{@code loadDetailsByCode}) and overrides
 * {@code authenticate} to add brute-force lockout. Also carries the application-specific account
 * lifecycle (registration with email + verification token, email verification, ban/unban).
 */
@Slf4j
@Service
public class BasicUserDetailsService extends dev.barrikeit.security.service.BasicUserDetailsService {

  private final UserRepository repository;
  private final UserMapper mapper;
  private final RoleRepository roleRepository;

  public BasicUserDetailsService(
      UserRepository repository, UserMapper mapper, RoleRepository roleRepository) {
    super(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    this.repository = repository;
    this.mapper = mapper;
    this.roleRepository = roleRepository;
  }

  // -------------------------------------------------------------------------
  // Core contract
  // -------------------------------------------------------------------------

  @Override
  protected BasicUserDetails loadDetails(String username) {
    return toDetails(findByUsername(username));
  }

  @Override
  protected BasicUserDetails loadDetailsByCode(UUID userId) {
    return toDetails(
        repository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, userId)));
  }

  /** Adds brute-force lockout on top of the standard credential check. */
  @Override
  public BasicUserDetails authenticate(String username, String rawPassword) {
    User user;
    try {
      user = findByUsername(username);
    } catch (NotFoundException e) {
      throw new UnauthorizedException("exception.auth.bad-credentials");
    }
    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      checkAttempts(user);
      throw new UnauthorizedException("exception.auth.bad-credentials");
    }
    if (!user.getSecurity().isEnabled()) {
      throw new UnauthorizedException("exception.auth.account-disabled");
    }
    if (user.getSecurity().isBanned()) {
      throw new UnauthorizedException("exception.auth.account-locked");
    }
    updateLoginDateAndResetAttempts(user);
    return toDetails(user);
  }

  private BasicUserDetails toDetails(User user) {
    return new BasicUserDetails(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.getSecurity().isEnabled(),
        user.getSecurity().isBanned(),
        getRoles(user),
        getAuthorities(user));
  }

  public User findByUsername(final String username) throws NotFoundException {
    return repository
        .findByUsernameEqualsIgnoreCase(username)
        .orElseThrow(
            () -> {
              log.info("Usuario {} no encontrado", username);
              return new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, username);
            });
  }

  // -------------------------------------------------------------------------
  // Application-specific account lifecycle
  // -------------------------------------------------------------------------

  public String register(final UserDto dto) {
    User user = validateUserToCreateUpdate(dto, true);
    generateUserForCreateUpdate(dto, user);
    user.setUsername(dto.getUsername());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    mapper.updateEntity(dto, user);
    user.getSecurity().setRegistrationDate(TimeUtil.offsetDateTimeNow());
    user.getSecurity().setVerificationToken(RandomUtil.randomBase64(14));

    user = repository.save(user);
    UserDto registeredUserDto = mapper.toDto(user);
    EmailUtil.sendEmail(registeredUserDto, EmailType.REGISTER_USER);
    return URLEncoder.encode(
        registeredUserDto.getSecurity().getVerificationToken(), StandardCharsets.UTF_8);
  }

  private User validateUserToCreateUpdate(UserDto dto, boolean isCreate) {
    User user = validateUsername(dto, isCreate);
    validateEmail(dto);
    return user;
  }

  private User validateUsername(UserDto dto, boolean isCreate) {
    User user = repository.findByUsernameEqualsIgnoreCase(dto.getUsername()).orElse(new User());
    if (isCreate && !user.isNew()) {
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
    if (registeredUser.getSecurity().isEnabled()) {
      throw new BadRequestException(ExceptionConstants.ERROR_USER_ALREADY_ENABLED);
    } else {
      registeredUser.getSecurity().setVerificationToken(null);
      registeredUser.getSecurity().setEnabled(true);
      repository.save(registeredUser);
    }
  }

  public void checkAttempts(final User user) {
    int loginAttempts = user.getSecurity().getLoginAttempts();
    if (loginAttempts < 10) {
      loginAttempts++;
      user.getSecurity().setLoginAttempts(loginAttempts);
    } else {
      banUser(user);
    }
    repository.save(user);
  }

  public User banUser(final User user) {
    user.getSecurity().setBanned(Boolean.TRUE);
    user.getSecurity().setBanDate(TimeUtil.offsetDateTimeNow());
    return repository.save(user);
  }

  public User unbanUser(final User user) {
    user.getSecurity().setBanned(Boolean.FALSE);
    user.getSecurity().setBanDate(null);
    return repository.save(user);
  }

  public User updateLoginDateAndResetAttempts(final User user) {
    return repository.save(resetAttempts(updateLoginDate(user)));
  }

  private User resetAttempts(final User user) {
    user.getSecurity().setBanned(false);
    user.getSecurity().setBanDate(null);
    user.getSecurity().setLoginAttempts(0);
    return user;
  }

  private User updateLoginDate(final User user) {
    user.getSecurity().setLoginDate(TimeUtil.offsetDateTimeNow());
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
