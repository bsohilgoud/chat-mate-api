package com.sohil.chatmate.projection;

import java.time.LocalDateTime;

public interface ConversationSummary {
    //TIP: This is JPA Projections and here we can read the table columns into this class (but we should create proper alias's)
    String getSenderId();
    String getReceiverId();
    LocalDateTime getTimestamp();
    String getContent();
    String getContentType();
    Integer getNewMessagesCount();
    String getPartnerFullName();
    String getPartnerId();
}
