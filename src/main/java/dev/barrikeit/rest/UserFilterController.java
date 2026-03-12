package dev.barrikeit.rest;

import lombok.extern.log4j.Log4j2;
import dev.barrikeit.model.domain.User;
import dev.barrikeit.rest.base.FilterBaseController;
import dev.barrikeit.service.UserFilterService;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.filter.UserFilter;
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
