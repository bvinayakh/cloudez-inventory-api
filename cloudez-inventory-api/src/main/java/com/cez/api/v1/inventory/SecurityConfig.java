package com.cez.api.v1.inventory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
  private String PUBLIC_ENDPOINT = "/public/**";
  private String PROTECTED_ENDPOINT = "/protected/**";

  @Value("${spring.security.debug:false}")
  private boolean securityDebug;

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception
  {
    return http.csrf(csrf -> csrf.disable()).authorizeRequests(auth -> {
      auth.antMatchers(PUBLIC_ENDPOINT).permitAll();
    }).httpBasic(Customizer.withDefaults()).build();
  }
}
