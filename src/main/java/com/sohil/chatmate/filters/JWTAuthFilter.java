package com.sohil.chatmate.filters;

import com.sohil.chatmate.helper.JWTHelper;
import com.sohil.chatmate.security.UserPrinciple;
import com.sohil.chatmate.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    JWTHelper jwtHelper;

    @Autowired
    UserService userService;

    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String userId = null;
        String token = null;

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                if (!jwtHelper.isTokenValid(token))
                    throw new BadCredentialsException("Invalid Access Token");

                userId = jwtHelper.getUserId(token);

            } catch (Exception e) {
                handlerExceptionResolver.resolveException(request, response, null, new BadCredentialsException("Invalid Access Token"));
            }
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserPrinciple userPrinciple = userService.getUserPrinciple(userId);
            try {
                if (jwtHelper.validateToken(token, userPrinciple)) {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userPrinciple, null, authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    handlerExceptionResolver.resolveException(request, response, null, new BadCredentialsException("Access Token expired or invalidated"));
                }
            } catch (ExpiredJwtException e) {
//                log.error("JWT Token has expired: {}", e.getMessage());
                handlerExceptionResolver.resolveException(request, response, null, new BadCredentialsException("JWT Token expired"));
                return;
            } catch (JwtException e) {
//                log.error("JWT Exception: {}", e.getMessage());
                handlerExceptionResolver.resolveException(request, response, null, new BadCredentialsException("JWT Token is invalid"));
                return;
            } catch (Exception e) {
//                log.error("Unexpected error during JWT validation", e);
                handlerExceptionResolver.resolveException(request, response, null, new BadCredentialsException("Authentication failed due to an unexpected error"));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
