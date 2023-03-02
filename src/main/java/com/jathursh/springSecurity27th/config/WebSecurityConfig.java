package com.jathursh.springSecurity27th.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

    private static final String[] WHITE_LIST_URLS = {
            "/hello",
            "/register",
            "/verifyRegistration*",
            "/resendVerificationToken*",
            "/resetPassword",
            "/savePassword",
            "/changePassword"
    };

    // define as bean so we can auto-wire it
    @Bean
    public PasswordEncoder passwordEncoder(){
      return new BCryptPasswordEncoder(11);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests((authorize -> authorize.requestMatchers(WHITE_LIST_URLS).permitAll()));
                //.authorizeHttpRequests().requestMatchers("/api/**").authenticated();
                //.requestMatchers(WHITE_LIST_URLS).permitAll();

        //disabling cors() and csrf() to enable posting
        return http.build();
    }

}
