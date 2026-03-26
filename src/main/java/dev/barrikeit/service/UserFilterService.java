package dev.barrikeit.service;

import dev.barrikeit.model.domain.User;
import dev.barrikeit.model.repository.UserRepository;
import dev.barrikeit.service.base.FilterBaseService;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.filter.UserFilter;
import dev.barrikeit.service.filter.UserFilterBuilder;
import dev.barrikeit.service.mapper.UserMapper;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Log4j2
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
