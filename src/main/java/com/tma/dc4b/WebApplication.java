package com.tma.dc4b;

import java.util.Map;
import javax.inject.Inject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
public class WebApplication extends WebSecurityConfigurerAdapter {

  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .logout()
          .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.ACCEPTED))
          .deleteCookies("JSESSIONID")
          .invalidateHttpSession(true).clearAuthentication(true).and()
        .authorizeRequests()
        .antMatchers("/index.html", "/", "/login").permitAll()
        .anyRequest().authenticated().and()
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
  }

  @Bean
  public RestTemplate loadBalancedRestTemplate(RestTemplateCustomizer customizer) {
    RestTemplate restTemplate = new RestTemplate();
    customizer.customize(restTemplate);
    return restTemplate;
  }

  @Inject
  private RestTemplate keyUriRestTemplate;

  private String getKeyFromAuthorizationServer() {
    HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
    return (String) this.keyUriRestTemplate
        .exchange("http://localhost:9999/oauth/token_key", HttpMethod.GET, request, Map.class)
        .getBody()
        .get("value");
  }
}
