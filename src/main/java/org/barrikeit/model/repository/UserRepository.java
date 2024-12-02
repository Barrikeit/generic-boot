package org.barrikeit.model.repository;

import java.util.Optional;
import org.barrikeit.model.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GenericRepository<User, Long> {

  Optional<User> findByUsernameEqualsIgnoreCase(String user);

  Optional<User> findByEmailEqualsIgnoreCase(String email);

  Optional<User> findByUsernameEqualsIgnoreCaseAndEmailEqualsIgnoreCase(String user, String email);
}
