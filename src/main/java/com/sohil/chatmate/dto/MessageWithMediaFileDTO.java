package com.sohil.chatmate.dto;

import com.sohil.chatmate.enums.ContentType;
import com.sohil.chatmate.enums.MessageStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record MessageWithMediaFileDTO(Long messageId, String senderId, String content, String receiverId, MultipartFile file, ContentType type, MessageStatus status, LocalDateTime timestamp) {
}
