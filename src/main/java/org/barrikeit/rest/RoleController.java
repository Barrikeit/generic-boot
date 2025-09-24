package org.barrikeit.rest;

import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.Role;
import org.barrikeit.rest.dto.RoleDto;
import org.barrikeit.service.RoleService;
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
}
