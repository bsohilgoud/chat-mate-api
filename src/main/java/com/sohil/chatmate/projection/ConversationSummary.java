package com.sohil.chatmate.projection;

import com.sohil.chatmate.enums.MessageStatus;

import java.time.LocalDateTime;

public interface ConversationSummary {
    //TIP: This is JPA Projections and here we can read the table columns into this class (but we should create proper alias's)
    String getSenderId();
    String getReceiverId();
    LocalDateTime getTimestamp();
    String getContent();
    String getContentType();
    MessageStatus getStatus();
    Integer getNewMessagesCount();
    String getPartnerId();
    String getPartnerFullName();
    String getPartnerProfileUrl();
    String getPartnerOnlineStatus();
    LocalDateTime getPartnerLastSeen();
}
