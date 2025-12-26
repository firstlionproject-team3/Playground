package org.example.playground.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth->oauth.defaultSuccessUrl("/" , true))
                .authorizeHttpRequests(auth-> auth.anyRequest().permitAll());
        return http.build();
    }

    //TODO: 패스워드 인코더 추가.
}
