package com.sohil.chatmate.dto;

import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.enums.ContentType;
import java.time.LocalDateTime;

public record UserMessageDTO(String senderId, String receiverId, String content, ContentType type, MessageStatus status, LocalDateTime timestamp) {
}
