package com.sohil.chatmate.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Enumeration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // comments
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors( cors -> cors.disable())
                .csrf(c -> c.disable())
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/auth/register", "/auth/login", "/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(f -> f.disable());

        return http.build();
    }
}
