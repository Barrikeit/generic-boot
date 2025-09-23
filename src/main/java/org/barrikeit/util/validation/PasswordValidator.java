package org.barrikeit.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // (?=.*[0-9]) # a digit must occur at least once
    // (?=.*[a-z]) # a lower case letter must occur at least once
    // (?=.*[A-Z]) # an upper case letter must occur at least once
    // (?=.*[@#$%^&+=]) # a special character must occur at least once
    // (?=\S+$) # no whitespace allowed in the entire string
    // .{8,} # anything, at least eight places though

    return value == null
        || value.isEmpty()
        || value.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
  }
}
