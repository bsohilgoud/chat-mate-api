package com.sohil.chatmate.listeners;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpEventListeners implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println(String.format("Session Created: ID={}", event.getSession().getId()));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println(String.format("Session Destroyed: ID={}", event.getSession().getId()));
    }

}
