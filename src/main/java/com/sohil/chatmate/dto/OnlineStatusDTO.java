package com.sohil.chatmate.dto;

import java.time.LocalDateTime;

public record OnlineStatusDTO(String userId, LocalDateTime lastSeen, String status) {
}
