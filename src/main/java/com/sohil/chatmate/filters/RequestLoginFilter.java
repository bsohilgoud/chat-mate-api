package com.sohil.chatmate.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Component
@Slf4j
public class RequestLoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        Enumeration<String> headersNames = httpServletRequest.getHeaderNames();
//        headersNames.asIterator().forEachRemaining(h -> System.out.println("httpServletRequest = " + httpServletRequest.getHeader(h)));
//        System.out.println(String.format("Incoming request: [{}] {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI()));
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
