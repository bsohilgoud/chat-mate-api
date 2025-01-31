package com.sohil.chatmate.entity;

import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.enums.ContentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@Table(name = "messages")
public class Message {

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

    @Column(name = "content_type")
    ContentType contentType;

    @Column(name = "status")
    MessageStatus status;

}
