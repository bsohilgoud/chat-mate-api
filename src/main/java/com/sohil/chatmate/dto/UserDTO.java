package com.sohil.chatmate.dto;

import java.time.LocalDateTime;

public record UserDTO(String id, String email, String username, String phoneNumber, String fullName, String profileUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
