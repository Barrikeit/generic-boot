package org.barrikeit.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.Role;
import org.barrikeit.model.domain.User;
import org.barrikeit.model.repository.RoleRepository;
import org.barrikeit.model.repository.UserRepository;
import org.barrikeit.rest.dto.UserDto;
import org.barrikeit.service.mapper.UserMapper;
import org.barrikeit.util.RandomUtil;
import org.barrikeit.util.TimeUtil;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.BadRequestException;
import org.barrikeit.util.exceptions.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class UserService extends GenericCodeService<User, Long, UUID, UserDto> {
  private final UserRepository repository;
  private final UserMapper mapper;

  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public UserService(UserRepository repository, UserMapper mapper, RoleRepository roleRepository) {
    super(repository, mapper);
    this.repository = repository;
    this.mapper = mapper;
    this.roleRepository = roleRepository;
  }

  private User findByUsername(final String username) {
    return repository
        .findByUsernameEqualsIgnoreCase(username)
        .orElseThrow(() -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, username));
  }

  public UserDto findDtoByUsername(final String username) {
    User user = findByUsername(username);
    return mapper.toDto(user);
  }

  @Override
  @Transactional
  public UserDto save(UserDto dto) {
    User user = validateUserToCreateUpdate(dto, true);
    generateUserForCreateUpdate(dto, user);
    user.setUsername(dto.getUsername());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    mapper.updateEntity(dto, user);
    user.setRegistrationDate(TimeUtil.localDateTimeNow());
    user.setVerificationToken(RandomUtil.getRandomBase64EncodedString(14));

    repository.save(user);
    sendEmail(user, "CREATE_USER");
    return mapper.toDto(user);
  }

  @Override
  @Transactional
  public UserDto updateByCode(UUID code, UserDto dto) {
    validateToggleActivationUser(dto, true);
    User user = validateUserToCreateUpdate(dto, false);
    generateUserForCreateUpdate(dto, user);
    mapper.updateEntity(dto, user);

    repository.save(user);
    sendEmail(user, "UPDATE_USER");
    return mapper.toDto(user);
  }

  @Transactional
  public UserDto toggleActivationUser(UserDto dto) {
    validateToggleActivationUser(dto, false);
    User user = validateUserToCreateUpdate(dto, false);
    user.setEnabled(!dto.isEnabled());
    user.setBanned(dto.isBanned());
    user.setBanReason(dto.getBanReason());

    repository.save(user);
    sendEmail(user, !dto.isEnabled() ? "ENABLE_USER" : "DISABLE_USER");
    return mapper.toDto(user);
  }

  public User validateUserToCreateUpdate(UserDto dto, boolean isCreate) {
    User user = validateUserName(dto, isCreate);
    validateMail(dto);
    return user;
  }

  /**
   * Un user no puede desactivarse a si mismo, se lanza BadRequestException en tal caso
   *
   * @param dto: user que se pretende activar/desactivar
   * @param isUpdate: si vale true se esta actualizando el user, si vale false se esta haciendo
   *     toggle de la propiedad habilitado
   */
  private void validateToggleActivationUser(UserDto dto, boolean isUpdate) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String authenticatedUser = authentication.getName();

    if (dto.getUsername().equals(authenticatedUser)
        && ((isUpdate && !dto.isEnabled()) || (!isUpdate && dto.isEnabled()))) {
      throw new BadRequestException(
          ExceptionConstants.ERROR_USER_DEACTIVATE_HIMSELF, dto.getUsername());
    }
  }

  private User validateUserName(UserDto dto, boolean isCreate) {
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

  private void validateMail(UserDto dto) {
    if (repository.findByEmailEqualsIgnoreCase(dto.getEmail()).isPresent()) {
      throw new BadRequestException(
          ExceptionConstants.ERROR_USER_EMAIL_ALREADY_EXISTS, dto.getEmail());
    }
  }

  public void generateUserForCreateUpdate(UserDto dto, User user) {
    validateRol(dto, user);
    // add any other validations if needed
  }

  private void validateRol(UserDto dto, User user) {
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

  private void sendEmail(final User user, final String mailTipo) {}
}
