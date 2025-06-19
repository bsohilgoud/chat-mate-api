package com.sohil.chatmate.dto.request;

import com.sohil.chatmate.enums.NotificationType;

public record NotificationRequest(String fromUser, String toUser, NotificationType type) {

}
