package com.sohil.chatmate.controller;

import com.sohil.chatmate.dto.BulkStatusUpdateRequestDTO;
import com.sohil.chatmate.dto.StatusUpdateRequestDTO;
import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.helper.WSMessagesHelper;
import com.sohil.chatmate.projection.LastConversation;
import com.sohil.chatmate.service.MessageService;
import com.sohil.chatmate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

public class MessageController {

    UserService userService;
    MessageService messageService;

    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }


    /* TIP: GET /messages/{user_id}
           {user_id} → Path variable (ID of the other user you are chatting with).
           login_person_user_id → Sent in the request body.
           ❌ Issues with this approach:
               - GET requests should not have a body
               - GET requests should not contain a request body because HTTP GET is meant for fetching data.
            Note: Some HTTP clients (browsers, caching systems) ignore or strip bodies from GET requests.
           ✅ The server can infer login_person_user_id from authentication extract it from the authenticated user.
     */
    @GetMapping("/{recipientId}")
    public ResponseEntity<List<UserMessageDTO>> getChatMessages(@PathVariable("recipientId") String recipientId) {
        try {
            return ResponseEntity.ok(messageService.getChatMessages(recipientId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/recent")
    public ResponseEntity<List<UserMessageDTO>> getRecentMessage() {
        try {
            return ResponseEntity.ok(messageService.getUnreadMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<List<LastConversation>> getLatestMessages() {
        try {
            return ResponseEntity.ok(messageService.getLastMessages());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/new")
    public ResponseEntity<UserMessageDTO> receivedNewMessage(@RequestBody UserMessageDTO newMessage) {
        UserMessageDTO userMessageDTO = messageService.newMessage(newMessage);
        return ResponseEntity.ok(userMessageDTO);
    }

    @PatchMapping("/status/{messageId}")
    public ResponseEntity<?> updateMessageStatus(@PathVariable Long messageId, @RequestBody StatusUpdateRequestDTO statusUpdateRequestDTO) {
        try {
            messageService.updateMessageStatus(messageId, statusUpdateRequestDTO.status());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/status/bulk")
    public ResponseEntity<?> bulkMessageStatusUpdate(@RequestBody BulkStatusUpdateRequestDTO bulkStatusUpdateRequestDTO) {
        try {
            messageService.bulkMessageStatusUpdate(bulkStatusUpdateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
