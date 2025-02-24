package com.sohil.chatmate.dto;


import com.sohil.chatmate.enums.MessageStatus;

public record UserShortMessages (String userId, String displayName, LatestMessage latestMessage) {
    public record LatestMessage(String message, String time, MessageStatus status){}
}


