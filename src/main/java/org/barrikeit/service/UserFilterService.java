package org.barrikeit.service;

import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.User;
import org.barrikeit.model.repository.UserRepository;
import org.barrikeit.service.base.FilterBaseService;
import org.barrikeit.service.dto.UserDto;
import org.barrikeit.service.filter.UserFilter;
import org.barrikeit.service.filter.UserFilterBuilder;
import org.barrikeit.service.mapper.UserMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class UserFilterService extends FilterBaseService<User, Long, UserDto, UserFilter> {

  public UserFilterService(UserRepository repository, UserMapper mapper) {
    super(repository, mapper);
  }

  @Override
  public UserFilterBuilder instanceFilterBuilder(Pageable page, String search) {
    return new UserFilterBuilder(page, search);
  }
}
