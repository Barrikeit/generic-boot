package org.barrikeit.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;


public class SanitizeValidator implements ConstraintValidator<Sanitize, String> {

  @Override
  public void initialize(Sanitize constraintAnnotation) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    String cleanedValue = Jsoup.clean(value, Safelist.basic());

    return cleanedValue.equals(value);
  }
}
