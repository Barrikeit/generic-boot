package dev.barrikeit.security.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
  ACCESS,
  REFRESH
}
