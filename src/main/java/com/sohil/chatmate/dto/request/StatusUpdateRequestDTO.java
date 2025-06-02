package com.sohil.chatmate.dto.request;

import com.sohil.chatmate.enums.MessageStatus;

public record StatusUpdateRequestDTO(MessageStatus status) {
}
