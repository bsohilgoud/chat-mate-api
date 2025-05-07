package com.sohil.chatmate.dto;

import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.enums.ContentType;
import java.time.LocalDateTime;

public record UserMessageDTO(Long messageId, String senderId, String receiverId, String content, ContentType type, MediaFileDTO mediaFileDTO, MessageStatus status, LocalDateTime timestamp) {
}