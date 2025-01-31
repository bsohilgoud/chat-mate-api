package com.sohil.chatmate.entity;

import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.enums.MessageType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "sender_id")
    String senderId;

    @Column(name = "receiver_id")
    String receiverId;

    @Column(name = "content")
    String content;

    @Column(name = "timestamp")
    LocalDateTime timestamp;

    //TODO: WE need to support this in future
//    @Column(name = "media")
//    MultipartFile media;

    @Column(name = "type")
    MessageType type;

    @Column(name = "status")
    MessageStatus status;

}
