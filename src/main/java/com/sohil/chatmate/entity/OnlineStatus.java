package com.sohil.chatmate.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "online_status")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OnlineStatus {

    @Id
    @Column(name = "user_id")
    private String id; // This will be the user_id

    @OneToOne
    @JoinColumn(name = "user_id")
    @MapsId // Maps the id property to the User's primary key
    private User user;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    public enum StatusType {
        ONLINE, OFFLINE;
    }
}
