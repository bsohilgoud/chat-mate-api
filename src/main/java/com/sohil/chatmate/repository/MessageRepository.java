package com.sohil.chatmate.repository;

import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("UPDATE Message m SET m.status = :messageStatus where m.id = :id" )
    void updateMessageStatus(Long id, MessageStatus messageStatus);
}
