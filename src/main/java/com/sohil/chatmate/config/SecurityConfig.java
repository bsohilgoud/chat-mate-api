package com.sohil.chatmate.config;

import com.sohil.chatmate.handlers.CustomAccessDeniedHandler;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/user/register", "/user/login").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .exceptionHandling(exception -> exception.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                                    System.out.println("Came Here");
                                    Enumeration<String> headersNames = request.getHeaderNames();
                                    headersNames.asIterator().forEachRemaining(h -> System.out.println("httpServletRequest = " + request.getHeader(h)));
                                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                                }
                        )
                )
                .formLogin(f -> f.disable());

        return http.build();
    }
}
