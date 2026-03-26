package dev.barrikeit.service;

import lombok.extern.log4j.Log4j2;
import dev.barrikeit.model.domain.Role;
import dev.barrikeit.model.repository.RoleRepository;
import dev.barrikeit.service.base.GenericCodeService;
import dev.barrikeit.service.dto.RoleDto;
import dev.barrikeit.service.mapper.RoleMapper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RoleService extends GenericCodeService<Role, Long, String, RoleDto> {
  private final RoleRepository repository;
  private final RoleMapper mapper;

  public RoleService(RoleRepository repository, RoleMapper mapper) {
    super(repository, mapper);
    this.repository = repository;
    this.mapper = mapper;
  }
}
