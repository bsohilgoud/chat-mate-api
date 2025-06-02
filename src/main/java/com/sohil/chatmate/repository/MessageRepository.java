package com.sohil.chatmate.repository;

import com.sohil.chatmate.entity.Message;
import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.projection.ConversationSummary;
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


    // TODO: We need sort the messages with timestamp (asc)
    @Query("SELECT m from Message m" +
            " WHERE (m.senderId = :senderId AND m.receiverId= :receiverId)" +
            " OR (m.senderId = :receiverId AND m.receiverId= :senderId) " +
            " ORDER BY timestamp ASC")
    List<Message> findChatMessages(@Param("senderId") String senderId, @Param("receiverId") String receiverId);

    @Query("SELECT m from Message m WHERE m.receiverId = :receiverId AND m.status = 'DELIVERED'")
    List<Message> getUnreadMessages(@Param("receiverId") String receiverId);

    @Query(value = """
            WITH all_conversations AS (
                SELECT
                    sender_id,
                    receiver_id,
                    timestamp,
            	    status
                FROM messages
                WHERE sender_id = :userId
                   OR receiver_id = :userId
            	ORDER BY sender_id
            ),
                        
            latest_conversations AS (
                SELECT
                    CASE
                        WHEN receiver_id = :userId THEN sender_id
                        ELSE receiver_id
                    END AS conversation_partner_id,
            		SUM(CASE WHEN status='DELIVERED' and receiver_id=:userId THEN 1 ELSE 0 END) as new_messages_count,
                    MAX(timestamp) AS last_message_time
                FROM all_conversations
                GROUP BY conversation_partner_id
            ),
                        
            latest_messages AS (
            	SELECT
            		m.sender_id,
            		m.receiver_id,
            		m.timestamp,
            		m.content,
            	    m.content_type,
            		lc.new_messages_count
            	FROM messages m
            	JOIN latest_conversations lc
            		ON (m.timestamp = lc.last_message_time
            			AND (
            				(m.sender_id = :userId AND m.receiver_id = lc.conversation_partner_id)
            				OR
            				(m.receiver_id = :userId AND m.sender_id = lc.conversation_partner_id)
            			)
            		)
            	ORDER BY m.timestamp DESC
            )
                        
            SELECT
            	lm.sender_id as senderId,
            	lm.receiver_id as receiverId,
            	lm.timestamp as timestamp,
            	lm.content as content,
            	lm.content_type as contentType,
            	lm.new_messages_count as newMessagesCount,
                u.user_id AS partnerId,
                u.full_name AS partnerFullName,
                u.profile_url AS partnerProfileUrl
            FROM latest_messages lm
            INNER JOIN users u ON (u.user_id=lm.receiver_id or u.user_id=lm.sender_id) and u.user_id !=:userId
            """, nativeQuery = true)
    List<ConversationSummary> getLastConversations(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.status = :toStatus WHERE m.senderId = :partnerId and m.receiverId = :userId and m.status = :fromStatus")
    void bulkStatusUpdate(@Param("userId") String userId, @Param("partnerId") String partnerId, @Param("fromStatus") MessageStatus fromStatus, @Param("toStatus") MessageStatus toStatus);

    /*
        WITH last_received_sent as (SELECT sender_id, receiver_id, MAX(timestamp) as timestamp from messages
        WHERE receiver_id='ff1f145b-9dd1-447c-91af-7502c17775e3' or sender_id='ff1f145b-9dd1-447c-91af-7502c17775e3'
        GROUP BY sender_id, receiver_id
        ORDER BY sender_id),

        last_conversations AS (
          SELECT
            CASE
              WHEN sender_id = 'ff1f145b-9dd1-447c-91af-7502c17775e3' THEN receiver_id ELSE sender_id
            END AS partner_id,
            MAX(timestamp) AS timestamp
          FROM last_received_sent
          WHERE sender_id = 'ff1f145b-9dd1-447c-91af-7502c17775e3' OR receiver_id = 'ff1f145b-9dd1-447c-91af-7502c17775e3'
          GROUP BY partner_id
        )

        SELECT * from last_conversations lc
        INNER JOIN messages m ON (
          (m.sender_id = 'ff1f145b-9dd1-447c-91af-7502c17775e3' AND m.receiver_id = lc.partner_id) OR
          (m.receiver_id = 'ff1f145b-9dd1-447c-91af-7502c17775e3' AND m.sender_id = lc.partner_id)
        ) AND m.timestamp = lc.timestamp
     */
}
