package org.barrikeit.rest;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.Role;
import org.barrikeit.rest.base.GenericCodeController;
import org.barrikeit.service.RoleService;
import org.barrikeit.service.dto.Response;
import org.barrikeit.service.dto.RoleDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/roles")
public class RoleController extends GenericCodeController<Role, Integer, String, RoleDto> {

  private final RoleService service;

  public RoleController(RoleService service) {
    super(service);
    this.service = service;
  }

  @Override
  @GetMapping(params = {"!page", "!size", "!sort", "!search"})
  public Response<List<RoleDto>> findAll() {
    return super.findAll();
  }

  @Override
  @GetMapping("/id/{id}")
  public Response<RoleDto> findById(@PathVariable("id") Integer id) {
    return super.findById(id);
  }

  @Override
  @GetMapping("/code/{code}")
  public Response<RoleDto> findByCode(@PathVariable("code") String code) {
    return super.findByCode(code);
  }
}
