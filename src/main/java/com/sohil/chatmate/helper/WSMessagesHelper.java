package com.sohil.chatmate.helper;

import com.sohil.chatmate.dto.UserMessageDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WSMessagesHelper {

    SimpMessagingTemplate simpMessagingTemplate;

    WSMessagesHelper(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendNewPrivateMessage(UserMessageDTO userMessageDTO){
        String receiverId = userMessageDTO.receiverId();
        System.out.println("sending new message: " +  userMessageDTO );
        simpMessagingTemplate.convertAndSend("/queue/private/" + receiverId, userMessageDTO);
    }
}
