package dev.barrikeit.rest;

import dev.barrikeit.model.domain.Role;
import dev.barrikeit.rest.base.GenericCodeController;
import dev.barrikeit.rest.base.Response;
import dev.barrikeit.service.RoleService;
import dev.barrikeit.service.dto.RoleDto;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/roles")
public class RoleController extends GenericCodeController<Role, Long, String, RoleDto>
    implements RoleApi {

  private final RoleService service;

  public RoleController(RoleService service) {
    super(service);
    this.service = service;
  }

  @Override
  @GetMapping(params = {"!page", "!size", "!sort", "!search"})
  public Response<List<RoleDto>> findAll() {
    return Response.error(HttpStatus.FORBIDDEN, "Endpoint disabled");
  }

  @Override
  @GetMapping("/id/{id}")
  public Response<RoleDto> findById(@PathVariable("id") Long id) {
    return Response.error(HttpStatus.FORBIDDEN, "Endpoint disabled");
  }

  @Override
  @GetMapping("/code/{code}")
  public Response<RoleDto> findByCode(@PathVariable("code") String code) {
    return super.findByCode(code);
  }
}
