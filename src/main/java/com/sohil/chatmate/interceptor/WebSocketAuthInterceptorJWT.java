package com.sohil.chatmate.interceptor;

import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptorJWT implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        System.out.println("Inside WS interceptor >>>>>>>>>>>>>>>>>>>>> ");
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        HttpSession session = (HttpSession) accessor.getSessionAttributes().get("SPRING_SECURITY_CONTEXT");
//
//        if (session != null) {
//            SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
//
//            if (securityContext != null && securityContext.getAuthentication() != null) {
//                SecurityContextHolder.setContext(securityContext);
//                accessor.setUser(securityContext.getAuthentication());
//            }
//        }

        return message;
    }
}
