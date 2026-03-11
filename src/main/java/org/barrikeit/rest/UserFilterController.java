package org.barrikeit.rest;

import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.User;
import org.barrikeit.rest.base.FilterBaseController;
import org.barrikeit.service.UserFilterService;
import org.barrikeit.service.dto.UserDto;
import org.barrikeit.service.filter.UserFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserFilterController extends FilterBaseController<User, Long, UserDto, UserFilter> {

  private final UserFilterService service;

  public UserFilterController(UserFilterService service) {
    super(service);
    this.service = service;
  }
}
