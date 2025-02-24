package com.sohil.chatmate.controller;

import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.dto.UserShortMessages;
import com.sohil.chatmate.service.MessageService;
import com.sohil.chatmate.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    UserService userService;

    MessageService messageService;

    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    /**
     *   [{"friendUserId", "displayName", {"latest_message" -> message, date, status}}
     *
     */
//    List<UserShortMessages> getLatestMessages(){
//        messageService.getLatestMessages();
//    }


     List<UserMessageDTO> getChatMessages(String senderId, String receiverId){
        return messageService.getChatMessages(senderId, receiverId);
     }

}
