package com.sohil.chatmate.controller;

import com.sohil.chatmate.dto.*;
import com.sohil.chatmate.projection.ConversationSummary;
import com.sohil.chatmate.service.MessageService;
import com.sohil.chatmate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

public class MessageController {

    UserService userService;
    MessageService messageService;

    @Autowired
    HttpServletRequest httpServletRequest;

    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserMessageDTO>> addNewMessage(@RequestBody UserMessageDTO newMessage) {
        UserMessageDTO userMessageDTO = messageService.newMessage(newMessage);
        return ApiResponse.success(HttpStatus.OK.value(), userMessageDTO, httpServletRequest.getRequestURI());
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
    @GetMapping("/conversations/{recipientId}")
    public ResponseEntity<ApiResponse<List<UserMessageDTO>>> getConversationForUser(@PathVariable("recipientId") String recipientId) throws Exception {
        List<UserMessageDTO> chatMessages = messageService.getChatMessages(recipientId);

        return ApiResponse.success(HttpStatus.OK.value(), chatMessages, httpServletRequest.getRequestURI());
    }

    @GetMapping("/conversations/summary")
    public ResponseEntity<ApiResponse<List<ConversationSummary>>> get() throws Exception {
        List<ConversationSummary> conversationSummaries = messageService.getConversationSummary();

        return ApiResponse.success(HttpStatus.OK.value(), conversationSummaries, httpServletRequest.getRequestURI());

    }

    @PatchMapping("/status/{messageId}")
    public ResponseEntity<?> updateMessageStatus(@PathVariable("messageId") Long messageId, @RequestBody StatusUpdateRequestDTO statusUpdateRequestDTO) {
        messageService.updateMessageStatus(messageId, statusUpdateRequestDTO.status());
        return ApiResponse.success(HttpStatus.OK.value(), null, "Updated the status of the message with id: " + messageId, httpServletRequest.getRequestURI());
    }

    @PostMapping("/status/batch")
    public ResponseEntity<?> batchMessageStatusUpdate(@RequestBody BatchStatusUpdateRequestDTO batchStatusUpdateRequestDTO) throws Exception {
        messageService.batchMessageStatusUpdate(batchStatusUpdateRequestDTO);
        return ApiResponse.success(HttpStatus.OK.value(), null, httpServletRequest.getRequestURI());
    }

    @PostMapping("/media")
    public ResponseEntity<ApiResponse<UserMessageDTO>> uploadMediaFile(@ModelAttribute MessageWithMediaFileDTO messageWithMediaFileDTO) throws IOException {
        UserMessageDTO userMessageDTO = messageService.newMessageWithMediaFile(messageWithMediaFileDTO);
        return ApiResponse.success(HttpStatus.OK.value(), userMessageDTO, httpServletRequest.getRequestURI());
    }

    @GetMapping("/media/{fileName}")
    public ResponseEntity<byte[]> getMediaFile(@PathVariable("fileName") String fileName) throws IOException {
        byte[] mediaFile = messageService.getMediaFile(fileName);
        String contentType = Files.probeContentType(Path.of(fileName));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(mediaFile);
    }

    @PostMapping("/typing")
    public void userTyping(@RequestBody String receiverId){
        messageService.sendUserTypingNotification(receiverId);
    }
}
