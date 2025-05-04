package com.sohil.chatmate.enums;


public enum MessageStatus {
    // PENDING -> not sent from user (due to network)
    // DELIVERED -> once we added to the DB
    // READ -> Once user click on user chat
    PENDING, DELIVERED, READ, DELETED;
}
