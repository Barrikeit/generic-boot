package dev.barrikeit.security.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Security-only representation of an authenticated user. */
@Getter
public class BasicUserDetails implements UserDetails {

  @Serial private static final long serialVersionUID = 1L;
  private final UUID id;
  private final String username;
  @JsonIgnore private final String password;
  private final boolean enabled;
  private final boolean banned;
  private final Collection<? extends GrantedAuthority> roles;
  private final Collection<? extends GrantedAuthority> authorities;

  public BasicUserDetails(
      UUID id,
      String username,
      String password,
      boolean enabled,
      boolean banned,
      List<? extends GrantedAuthority> roles,
      List<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.enabled = enabled;
    this.banned = banned;
    this.roles = roles;
    this.authorities = authorities;
  }

  public List<String> getRolesNames() {
    return this.getRoles().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(Objects::nonNull)
        .toList();
  }

  public List<String> getAuthorityNames() {
    return this.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !banned;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof BasicUserDetails that && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
