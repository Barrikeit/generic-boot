package org.barrikeit.rest;

import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.User;
import org.barrikeit.rest.dto.Response;
import org.barrikeit.rest.dto.UserDto;
import org.barrikeit.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserController extends GenericCodeController<User, Long, UUID, UserDto> {
  private final UserService service;

  public UserController(UserService service) {
    super(service);
    this.service = service;
  }

  @GetMapping("/{username}")
  public Response<UserDto> findById(@PathVariable("username") String username) {
    return Response.ok(service.findDtoByUsername(username));
  }
}
