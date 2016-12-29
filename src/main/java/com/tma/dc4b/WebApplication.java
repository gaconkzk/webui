package com.tma.dc4b;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }

  @Configuration
  @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
  protected static class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .httpBasic().and()
          .logout().and()
          .authorizeRequests()
            .antMatchers("/index.html", "/").permitAll()
            .anyRequest().authenticated().and()
          .csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    /**
     * We need ignore the polymer resource
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
      web
          .ignoring().antMatchers("/resources/**", "/css/**", "/images/**");
    }
  }

  @GetMapping("/user")
  public Principal user(Principal user) {
    return user;
  }

  @GetMapping("/token")
  public Map<String, String> token(HttpSession session) {
    return Collections.singletonMap("token", session.getId());
  }
}
