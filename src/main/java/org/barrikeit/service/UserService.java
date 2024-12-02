package org.barrikeit.service;

import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.User;
import org.barrikeit.model.repository.UserRepository;
import org.barrikeit.service.dto.UserDto;
import org.barrikeit.service.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class UserService extends GenericService<User, Long, UserDto> {
  private final UserRepository repository;
  private final UserMapper mapper;

  public UserService(UserRepository repository, UserMapper mapper) {
    super(repository, mapper);
    this.repository = repository;
    this.mapper = mapper;
  }
}
