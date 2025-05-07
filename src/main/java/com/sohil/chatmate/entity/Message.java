package com.sohil.chatmate.entity;

import com.sohil.chatmate.enums.MessageStatus;
import com.sohil.chatmate.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "media_id")
    Media media;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    ContentType contentType;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    MessageStatus status;

}
