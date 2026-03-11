package org.barrikeit.model.repository;

import java.util.Optional;
import java.util.UUID;
import org.barrikeit.model.domain.User;
import org.barrikeit.model.repository.base.FilterBaseRepository;
import org.barrikeit.model.repository.base.GenericCodeRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends GenericCodeRepository<User, Long, UUID>, FilterBaseRepository<User> {

  Optional<User> findByUsernameEqualsIgnoreCase(String user);

  Optional<User> findByEmailEqualsIgnoreCase(String email);

  Optional<User> findByUsernameEqualsIgnoreCaseAndEmailEqualsIgnoreCase(String user, String email);

  Optional<User> findByVerificationToken(String token);
}
