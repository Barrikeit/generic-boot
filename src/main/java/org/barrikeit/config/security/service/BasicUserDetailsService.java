package org.barrikeit.config.security.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.AllArgsConstructor;
import org.barrikeit.config.security.model.domain.BasicUserDetails;
import org.barrikeit.config.security.rest.dto.RegisterDto;
import org.barrikeit.model.domain.Module;
import org.barrikeit.model.domain.Role;
import org.barrikeit.model.domain.User;
import org.barrikeit.model.repository.UserRepository;
import org.barrikeit.rest.dto.RoleDto;
import org.barrikeit.rest.dto.UserDto;
import org.barrikeit.service.UserService;
import org.barrikeit.service.mapper.UserMapper;
import org.barrikeit.util.TimeUtil;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.BadRequestException;
import org.barrikeit.util.exceptions.NotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BasicUserDetailsService {

  private final UserService userService;
  private final UserRepository repository;
  private final UserMapper mapper;

  public User findByUsername(final String username) throws NotFoundException {
    Optional<User> user = repository.findByUsernameEqualsIgnoreCase(username);
    if (user.isEmpty()) {
      throw new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, username);
    }
    return user.get();
  }

  public UserDto findDtoByUsername(final String username) throws NotFoundException {
    User user = findByUsername(username);
    return mapper.toDto(user);
  }

  public UserDetails loadUser(final String username) {
    User user = findByUsername(username);
    return new BasicUserDetails(
        user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        user.isBanned(),
        getRoles(user),
        getAuthorities(user));
  }

  public String register(final RegisterDto registerDto) {
    UserDto dto =
        userService.save(
            UserDto.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .roles(Set.of(RoleDto.builder().code("US").build()))
                .build());
    return URLEncoder.encode(dto.getVerificationToken(), StandardCharsets.UTF_8);
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
      userService.save(registeredUser);
    }
  }

  public UserDetails save(final UserDto dto) {
    User user = mapper.toEntity(dto);
    user = repository.save(user);
    return new BasicUserDetails(user);
  }

  public UserDetails update(UserDto dto, User entity) {
    UserDto userDto = mapper.toDto(entity);
    mapper.updateDto(userDto, dto);
    return save(userDto);
  }

  public User enableUser(final User user) {
    user.setEnabled(Boolean.TRUE);
    return repository.save(user);
  }

  public User disableUser(final User user) {
    user.setEnabled(Boolean.FALSE);
    return repository.save(user);
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

  private Collection<? extends GrantedAuthority> getRoles(User user) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    if (user.getRoles() != null) {
      for (Role role : user.getRoles().stream().toList()) {
        authorities.add(new SimpleGrantedAuthority((role.getCode())));
      }
    } else {
      return Collections.emptyList();
    }
    return authorities;
  }

  private Collection<? extends GrantedAuthority> getAuthorities(User user) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    if (user.getRoles() != null) {
      for (Role role : user.getRoles().stream().toList()) {
        for (Module modulo : role.getModules().stream().toList()) {
          authorities.add(new SimpleGrantedAuthority(modulo.getCode()));
        }
      }
    } else {
      return Collections.emptyList();
    }
    return authorities;
  }
}
