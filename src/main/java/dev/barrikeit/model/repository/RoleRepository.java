package dev.barrikeit.model.repository;

import dev.barrikeit.model.domain.Role;
import dev.barrikeit.model.repository.base.GenericCodeRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends GenericCodeRepository<Role, Long, String> {}
