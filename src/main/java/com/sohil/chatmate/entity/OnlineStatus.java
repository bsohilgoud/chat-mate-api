package com.sohil.chatmate.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "online_status")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OnlineStatus {

    @Id
    @Column(name = "user_id")
    private String userId; // Using user_id as primary key

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    public enum StatusType {
        ONLINE, OFFLINE;
    }
}
