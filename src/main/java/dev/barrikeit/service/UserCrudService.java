package dev.barrikeit.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import dev.barrikeit.model.domain.Role;
import dev.barrikeit.model.domain.User;
import dev.barrikeit.model.repository.RoleRepository;
import dev.barrikeit.model.repository.UserRepository;
import dev.barrikeit.service.base.GenericCodeCrudService;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.mapper.UserMapper;
import dev.barrikeit.util.EmailUtil;
import dev.barrikeit.util.RandomUtil;
import dev.barrikeit.util.TimeUtil;
import dev.barrikeit.util.constants.ExceptionConstants;
import dev.barrikeit.util.enums.EmailType;
import dev.barrikeit.util.exceptions.BadRequestException;
import dev.barrikeit.util.exceptions.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class UserCrudService extends GenericCodeCrudService<User, Long, UUID, UserDto> {
  private final UserRepository repository;
  private final UserMapper mapper;

  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public UserCrudService(
      UserRepository repository,
      UserMapper mapper,
      RoleRepository roleRepository) {
    super(repository, mapper);
    this.repository = repository;
    this.mapper = mapper;
    this.roleRepository = roleRepository;
  }

  public UserDto findByUsername(final String username) {
    User user = findEntityByUsername(username);
    return mapper.toDto(user);
  }

  private User findEntityByUsername(final String username) {
    return repository
        .findByUsernameEqualsIgnoreCase(username)
        .orElseThrow(() -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, username));
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

    user = repository.save(user);
    UserDto registeredUserDto = mapper.toDto(user);
    EmailUtil.sendEmail(registeredUserDto, EmailType.REGISTER_USER);
    return registeredUserDto;
  }

  @Override
  @Transactional
  public UserDto updateByCode(UUID code, UserDto dto) {
    validateToggleActivationUser(dto, true);
    User user = validateUserToCreateUpdate(dto, false);
    generateUserForCreateUpdate(dto, user);
    mapper.updateEntity(dto, user);

    repository.save(user);
    UserDto modifiedUserDto = mapper.toDto(user);
    EmailUtil.sendEmail(modifiedUserDto, EmailType.UPDATED_USER);
    return modifiedUserDto;
  }

  @Transactional
  public UserDto toggleEnableUser(UserDto dto) {
    validateToggleActivationUser(dto, false);
    User user = validateUserToCreateUpdate(dto, false);
    user.setEnabled(!dto.isEnabled());
    user.setBanned(dto.isBanned());
    user.setBanReason(dto.getBanReason());

    repository.save(user);
    UserDto modifiedUserDto = mapper.toDto(user);
    EmailUtil.sendEmail(
        modifiedUserDto, !dto.isEnabled() ? EmailType.ENABLED_USER : EmailType.DISABLED_USER);
    return modifiedUserDto;
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
}
