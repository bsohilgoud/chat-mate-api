package com.sohil.chatmate.dto;

import com.sohil.chatmate.enums.MessageStatus;

public record BulkStatusUpdateRequestDTO (String partnerId, MessageStatus fromStatus, MessageStatus toStatus){
}
