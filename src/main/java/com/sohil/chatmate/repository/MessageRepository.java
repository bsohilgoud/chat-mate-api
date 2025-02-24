package com.sohil.chatmate.repository;

import com.sohil.chatmate.dto.UserMessageDTO;
import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.enums.MessageStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.status = :messageStatus WHERE m.id = :id")
    void updateMessageStatus(@Param("id") Long id, @Param("messageStatus") MessageStatus messageStatus);

//    @Query("")
//    void getLatestMessages(@Param("userId") String userId);

    // TODO: We need sort the messages with timestamp (asc)
    @Query("SELECT m from Message m " +
            "WHERE (m.senderId = :senderId AND m.receiverId= :receiverId)" +
            "OR (m.senderId = :receiverId AND m.receiverId= :senderId) ")
    List<Message> findChatMessages(@Param("senderId") String senderId, @Param("receiverId") String receiverId);
}
