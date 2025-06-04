package com.sohil.chatmate.dto;

import com.sohil.chatmate.entity.OnlineStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDTO {
    String id;
    String email;
    String username;
    String fullName;
    String phoneNumber;
    String profileUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    OnlineStatus.StatusType onlineStatus;
    LocalDateTime lastSeen;
}
