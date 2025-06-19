package com.sohil.chatmate.dto;

import com.sohil.chatmate.enums.NotificationType;

public record NotificationDTO(
        NotificationType type,
        Object fromUser,
        String toUser,
        Object body
) {}
