package com.sohil.chatmate.dto;

import java.time.LocalDateTime;

public record UserDTO(String id, String email, String username, String fullName, String phoneNumber, String profileUrl,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
}
