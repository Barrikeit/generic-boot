package org.barrikeit.service;

import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.Role;
import org.barrikeit.model.repository.RoleRepository;
import org.barrikeit.rest.dto.RoleDto;
import org.barrikeit.service.mapper.RoleMapper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RoleService extends GenericCodeService<Role, Integer, String, RoleDto> {
  private final RoleRepository repository;
  private final RoleMapper mapper;

  public RoleService(RoleRepository repository, RoleMapper mapper) {
    super(repository, mapper);
    this.repository = repository;
    this.mapper = mapper;
  }
}
