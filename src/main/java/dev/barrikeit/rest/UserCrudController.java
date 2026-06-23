package dev.barrikeit.rest;

import dev.barrikeit.model.domain.User;
import dev.barrikeit.rest.base.GenericController;
import dev.barrikeit.rest.base.Response;
import dev.barrikeit.service.UserCrudService;
import dev.barrikeit.service.dto.UserDto;
import java.util.List;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserCrudController extends GenericController<User, UUID, UserDto>
    implements UserCrudApi {

  private final UserCrudService service;

  public UserCrudController(UserCrudService service) {
    super(service);
    this.service = service;
  }

  @Override
  @GetMapping(params = {"!page", "!size", "!sort", "!search"})
  public Response<List<UserDto>> findAll() {
    return Response.error(HttpStatus.FORBIDDEN, "Endpoint disabled");
  }

  @Override
  @GetMapping("/id/{id}")
  public Response<UserDto> findById(@PathVariable("id") UUID id) {
    return Response.ok(service.find(id));
  }

  @Override
  @GetMapping("/{username}")
  public Response<UserDto> findByUsername(@PathVariable("username") String username) {
    return Response.ok(service.findByUsername(username));
  }
}
