package com.sohil.chatmate.controller;

import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.service.MessageService;
import com.sohil.chatmate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class MessageController {

    SimpMessagingTemplate simpMessagingTemplate;

    UserService userService;

    MessageService messageService;

    public MessageController(SimpMessagingTemplate simpMessagingTemplate, UserService userService, MessageService messageService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userService = userService;
        this.messageService = messageService;
    }

    @MessageMapping("/queue/connected")
    public UserMessageDTO userConnected(@Payload UserMessageDTO userMessageDTO) {
        return userMessageDTO;
    }

    // TIP: We don't need to specific the application prefix again here (/chat-mate/queue/)
    @MessageMapping("/queue/private")
    public void sendPrivateMessage(@Payload UserMessageDTO userMessage) {
        String receiverId = userMessage.receiverId();

        System.out.println("receiverId = " + receiverId);
        System.out.println("userMessage = " + userMessage);

        Optional<User> optionalReceiverUser = userService.findByUserId(receiverId);

        if (optionalReceiverUser.isEmpty())
            ResponseEntity.badRequest();

        Message savedMessage = messageService.saveMessage(userMessage);

        String destination = "/queue/private/" + receiverId;
        System.out.println("Sending message to destination: " + destination);

        // TIP: Since we are not using the sendToUser the subscription and destination should not include /user
        simpMessagingTemplate.convertAndSend(destination, userMessage);

        System.out.println("updating status of message with id: = " + savedMessage.getId());
        messageService.updateMessageStatus(savedMessage.getId(), MessageStatus.DELIVERED);
    }
}
