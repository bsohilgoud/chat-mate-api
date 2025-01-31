package com.sohil.chatmate.controller;

import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.service.MessageService;
import com.sohil.chatmate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

    @MessageMapping("/chat-mate/queue/connected")
    public void userConnected(){

    }

    @MessageMapping("/chat-mate/queue/private")
    public void sendPrivateMessage(UserMessageDTO userMessage){
        String receiverId = userMessage.receiverId();

        System.out.println("receiverId = " + receiverId);
        System.out.println("userMessage = " + userMessage);

        Optional<User> optionalReceiverUser = userService.findByUserId(receiverId);

        if (optionalReceiverUser.isEmpty())
            ResponseEntity.badRequest();

        System.out.println("optionalReceiverUser.get().getUserID() = " + optionalReceiverUser.get().getUserID());
        System.out.println("optionalReceiverUser.get().getDisplayName() = " + optionalReceiverUser.get().getDisplayName());
        Message savedMessage = messageService.saveMessage(userMessage);

        simpMessagingTemplate.convertAndSend("/user/queue/private/" + receiverId, userMessage);

        messageService.updateMessageStatus(savedMessage.getId(), MessageStatus.DELIVERED);
    }
}
