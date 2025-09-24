package org.barrikeit.model.repository;

import java.util.Optional;
import java.util.UUID;
import org.barrikeit.model.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GenericCodeRepository<User, Long, UUID> {

  Optional<User> findByUsernameEqualsIgnoreCase(String user);

  Optional<User> findByEmailEqualsIgnoreCase(String email);

  Optional<User> findByUsernameEqualsIgnoreCaseAndEmailEqualsIgnoreCase(String user, String email);

  Optional<User> findByVerificationToken(String token);
}
