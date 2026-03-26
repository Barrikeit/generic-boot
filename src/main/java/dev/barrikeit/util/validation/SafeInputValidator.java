package dev.barrikeit.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SafeInputValidator implements ConstraintValidator<SafeInput, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // allows: letters (any language), digits, and @#$%^&+=
    return value == null || value.isEmpty() || value.matches("^[\\p{L}\\d@#$%^&+=]+$");
  }
}
