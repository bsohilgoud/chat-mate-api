//package com.sohil.chatmate.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//
//import java.util.Map;
// TIP: This is not required
//@Component
//public class WSAuthInterceptor implements HandshakeInterceptor {
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//
////        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
////        HttpSession session = httpServletRequest.getSession();
////
////        SecurityContext securityContext  = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
////        Authentication authentication = securityContext.getAuthentication();
////
////        attributes.put("AUTHENTICATION", authentication);
//
//        return true;
//
////        if (request instanceof ServletServerHttpRequest servletRequest) {
////            HttpSession session = servletRequest.getServletRequest().getSession(false);
////            if (session != null) {
////                SecurityContext securityContext =
////                        (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
////                if (securityContext != null) {
////                    Authentication authentication = securityContext.getAuthentication();
////                    attributes.put("AUTHENTICATION", authentication);  // Store authentication for later
////                }
////            }
////        }
////        return true;  // Allow WebSocket connection
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
//
//    }
//}
