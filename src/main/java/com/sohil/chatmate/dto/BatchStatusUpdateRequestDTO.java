package com.sohil.chatmate.dto;

import com.sohil.chatmate.enums.MessageStatus;

public record BatchStatusUpdateRequestDTO(String partnerId, MessageStatus fromStatus, MessageStatus toStatus){
}
