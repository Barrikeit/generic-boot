package org.barrikeit.config.security.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Getter
public class CsrfConfiguration {
  private final RequestMatcher csrfRequestMatcher =
      new RequestMatcher() {
        private final Pattern allowedMethods = Pattern.compile("^(GET|POST|HEAD|TRACE|OPTIONS)$");
        private final RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/.*", null);

        @Override
        public boolean matches(final HttpServletRequest request) {
          return !allowedMethods.matcher(request.getMethod()).matches()
              && !apiMatcher.matches(request);
        }
      };
}
