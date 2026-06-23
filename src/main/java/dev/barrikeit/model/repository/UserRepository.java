package dev.barrikeit.model.repository;

import dev.barrikeit.model.domain.User;
import dev.barrikeit.springframework.data.repository.FilterBaseRepository;
import dev.barrikeit.springframework.data.repository.GenericRepository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GenericRepository<User, UUID>, FilterBaseRepository<User> {

  Optional<User> findByUsernameEqualsIgnoreCase(String user);

  Optional<User> findByEmailEqualsIgnoreCase(String email);

  Optional<User> findByUsernameEqualsIgnoreCaseAndEmailEqualsIgnoreCase(String user, String email);

  @Query("select u from User u inner join u.security s where s.verificationToken = :token")
  Optional<User> findByVerificationToken(String token);
}
