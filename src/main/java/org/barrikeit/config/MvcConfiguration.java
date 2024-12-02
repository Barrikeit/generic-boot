package org.barrikeit.config;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.util.constants.ConfigurationConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Log4j2
@EnableWebMvc
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(jsonConverter());
  }

  @Bean
  public HttpMessageConverter<Object> jsonConverter() {
    return new MappingJackson2HttpMessageConverter();
  }

  @Bean
  public HandlerMapping handlerMapping() {
    return new RequestMappingHandlerMapping();
  }

  @Bean
  public HandlerAdapter handlerAdapter() {
    return new RequestMappingHandlerAdapter();
  }

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable("generic");
  }
}
