package com.sohil.chatmate.repository;

import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.enums.MessageStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.status = :messageStatus WHERE m.id = :id")
    void updateMessageStatus(@Param("id") Long id, @Param("messageStatus") MessageStatus messageStatus);
}
