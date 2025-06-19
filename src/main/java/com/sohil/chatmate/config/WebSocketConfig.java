package com.sohil.chatmate.config;


import com.sohil.chatmate.interceptor.WebSocketAuthInterceptorJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    WebSocketAuthInterceptorJWT webSocketAuthInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws_server").setAllowedOrigins("http://localhost:5173");
//                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // TIP: Currently, we don't need this user destination prefix since we are not using the sendToUser option
        // registry.setUserDestinationPrefix("/user");
        registry.enableSimpleBroker("/queue");

        // TIP: Don't forgot the '/'
        registry.setApplicationDestinationPrefixes("/chat-mate");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
