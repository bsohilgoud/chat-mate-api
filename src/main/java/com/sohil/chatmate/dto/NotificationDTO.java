package com.sohil.chatmate.dto;

import com.sohil.chatmate.enums.NotificationType;

public record NotificationDTO(
        NotificationType type,
        String fromUser,
        String toUser,
        Object body
) {}
