package com.sohil.chatmate.controller;


import com.sohil.chatmate.dto.request.NotificationRequest;
import com.sohil.chatmate.enums.NotificationType;
import com.sohil.chatmate.helper.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/notification")
@RestController
public class NotificationController {
    NotificationService  notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public void userTypingNotification(@RequestBody NotificationRequest notificationRequest){
        notificationService.notification(notificationRequest.type())
                .fromUser(notificationRequest.fromUser())
                .toUser(notificationRequest.toUser())
                .send();
    }
}
