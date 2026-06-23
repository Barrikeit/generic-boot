package dev.barrikeit.rest;

import dev.barrikeit.model.domain.User;
import dev.barrikeit.rest.base.FilterBaseController;
import dev.barrikeit.service.UserFilterService;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.filter.UserFilter;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserFilterController extends FilterBaseController<User, UUID, UserDto, UserFilter>
    implements UserFilterApi {

  private final UserFilterService service;

  public UserFilterController(UserFilterService service) {
    super(service);
    this.service = service;
  }
}
