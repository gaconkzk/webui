package com.tma.dc4b;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
@Slf4j
public class WebApplication extends WebSecurityConfigurerAdapter {

  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .logout().clearAuthentication(false).addLogoutHandler((req, res, auth) -> {
      //serverLogout(req,res);
    }).logoutSuccessHandler((req, res, auth) -> {

    }).and()
        .authorizeRequests()
        .antMatchers("/index.html", "/", "/login").permitAll()
        .anyRequest().authenticated().and()
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
  }
}
