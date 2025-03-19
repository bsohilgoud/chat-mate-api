package com.sohil.chatmate.dto;

import com.sohil.chatmate.entity.OnlineStatus;

public record UpdateOnlineStatusRequestDTO(String userId, OnlineStatus.StatusType status) {
}
