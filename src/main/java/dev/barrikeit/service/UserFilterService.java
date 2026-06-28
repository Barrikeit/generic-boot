package dev.barrikeit.service;

import dev.barrikeit.model.domain.User;
import dev.barrikeit.model.repository.UserRepository;
import dev.barrikeit.service.base.FilterBaseService;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.filter.UserFilter;
import dev.barrikeit.service.filter.UserFilterBuilder;
import dev.barrikeit.service.mapper.UserMapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserFilterService extends FilterBaseService<User, UUID, UserDto, UserFilter> {

  public UserFilterService(UserRepository repository, UserMapper mapper) {
    super(repository, mapper);
  }

  @Override
  public UserFilterBuilder instanceFilterBuilder(Pageable page, String search) {
    return new UserFilterBuilder(page, search);
  }
}
