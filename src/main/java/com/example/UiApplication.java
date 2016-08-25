package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
public class UiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    /**
     * To service the authenticate() function we need to add a new endpoint to the backend:
     * <p/>
     * This is a useful trick in a Spring Security application.
     * If the "/user" resource is reachable then it will return the currently authenticated user (an Authentication), and otherwise Spring Security will intercept the request
     * and send a 401 response through an AuthenticationEntryPoint
     *
     * @param user
     * @return
     */
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/resource")
    public Map<String, Object> home() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

    /**
     * Handling the Login Request on the Server
     * <p/>
     * This is a standard Spring Boot application with Spring Security customization,
     * just allowing anonymous access to the static (HTML) resources (the CSS and JS resources are already accessible by default)
     * <p/>
     * CSRF: Angular has built in support for CSRF (which it calls "XSRF") based on cookies.
     * So on the server we need a custom filter that will send the cookie.
     * Angular wants the cookie name to be "XSRF-TOKEN" and Spring Security provides it as a request attribute by default,
     * so we just need to transfer the value from a request attribute to a cookie.
     */
    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .httpBasic()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/index.html", "/home.html", "/login.html", "/").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        }
    }
}
