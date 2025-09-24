package org.barrikeit.config.security.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.barrikeit.model.domain.Role;
import org.barrikeit.model.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class BasicUserDetails implements UserDetails {
  @Serial private static final long serialVersionUID = 1L;

  private final String username;
  @JsonIgnore private final String password;
  private final boolean enabled;
  private final boolean banned;
  private final Collection<? extends GrantedAuthority> roles;
  private final Collection<? extends GrantedAuthority> authorities;

  public BasicUserDetails(
      String username,
      String password,
      Boolean enabled,
      Boolean banned,
      Collection<? extends GrantedAuthority> roles,
      Collection<? extends GrantedAuthority> authorities) {
    this.username = username;
    this.password = password;
    this.enabled = enabled;
    this.banned = banned;
    this.roles = roles;
    this.authorities = authorities;
  }

  public BasicUserDetails(User user) {
    this(
        user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        user.isBanned(),
        user.getRoles() == null
            ? Collections.emptyList()
            : user.getRoles().stream().map(Role::getCode).map(SimpleGrantedAuthority::new).toList(),
        Collections.emptyList());
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
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !banned;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BasicUserDetails that = (BasicUserDetails) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
