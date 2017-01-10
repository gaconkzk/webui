package com.tma.dc4b;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
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
        .logout().clearAuthentication(true).invalidateHttpSession(true)
        .addLogoutHandler((req, res, auth) -> {
          serverLogout(req, res);
    }).logoutSuccessHandler((req, res, auth) -> {

    }).deleteCookies("JSESSIONID").and()
        .authorizeRequests()
        .antMatchers("/index.html", "/", "/login").permitAll()
        .anyRequest().authenticated().and()
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
  }

  @Autowired
  OAuth2ClientContext clientContext;

  @Autowired
  OAuth2ProtectedResourceDetails protectedResourceDetails;

  public OAuth2RestOperations oAuth2RestOperations() {
    OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(protectedResourceDetails,
        clientContext);
    return restTemplate;
  }

  private void serverLogout(HttpServletRequest req, HttpServletResponse res) {
    oAuth2RestOperations()
        .exchange("http://localhost:9999/uaa/logout", HttpMethod.POST, null, Void.class);
    clientContext.setAccessToken(null);
  }

  private String getKeyFromAuthorizationServer() {
    HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
    return (String) this.oAuth2RestOperations()
        .exchange("http://localhost:999/oauth/token", HttpMethod.POST, request, Map.class).getBody()
        .get("value");

  }
}
