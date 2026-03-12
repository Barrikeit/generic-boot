package dev.barrikeit.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailType {
  REGISTER_USER,
  VERIFY_USER,
  UPDATED_USER,
  ENABLED_USER,
  DISABLED_USER,
  BANNED_USER,
  UNBANNED_USER
}
