package org.barrikeit.config.logging;

import org.barrikeit.util.constants.ConfigurationConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    @Bean
    @Profile({ConfigurationConstants.SPRING_PROFILE_DEVELOPMENT})
    LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }
}

