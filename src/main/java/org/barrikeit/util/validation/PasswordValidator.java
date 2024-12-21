package org.barrikeit.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    // # start-of-string
    // (?=.*[0-9]) # a digit must occur at least once
    // (?=.*[a-z]) # a lower case letter must occur at least once
    // (?=.*[A-Z]) # an upper case letter must occur at least once
    // (?=.*[@#$%^&+=]) # a special character must occur at least once
    // (?=\S+$) # no whitespace allowed in the entire string
    // .{8,} # anything, at least eight places though
    // $ # end-of-string

    boolean noExistePassword = value == null || value.isEmpty();
    // NOTA: Solo en el caso de que exista el password se evalua la expresion regular
    return noExistePassword
        || value.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
  }
}
