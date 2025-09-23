package org.barrikeit.model.repository;

import org.barrikeit.model.domain.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends GenericCodeRepository<Role, Integer, String> {}
