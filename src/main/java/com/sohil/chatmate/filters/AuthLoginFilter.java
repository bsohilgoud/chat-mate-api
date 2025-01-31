package com.sohil.chatmate.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthLoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            System.out.println("Authenticated session ID: "+  session.getId());
        } else {
            System.out.println(String.format("No session found for request: [{}] {}", httpRequest.getMethod(), httpRequest.getRequestURI()));
        }

        chain.doFilter(request, response);
    }
}
