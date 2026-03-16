package dev.barrikeit.model.repository;

import dev.barrikeit.model.domain.User;
import dev.barrikeit.model.repository.base.FilterBaseRepository;
import dev.barrikeit.model.repository.base.GenericRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GenericRepository<User, UUID>, FilterBaseRepository<User> {

  Optional<User> findByUsernameEqualsIgnoreCase(String user);

  Optional<User> findByEmailEqualsIgnoreCase(String email);

  Optional<User> findByUsernameEqualsIgnoreCaseAndEmailEqualsIgnoreCase(String user, String email);

  Optional<User> findByVerificationToken(String token);
}
