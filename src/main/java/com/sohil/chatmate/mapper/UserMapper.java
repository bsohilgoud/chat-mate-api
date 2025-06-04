package com.sohil.chatmate.mapper;

import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.entity.User;

public class UserMapper {
    public static UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profileUrl(user.getProfileUrl())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .onlineStatus(user.getOnlineStatus().getStatus())
                .lastSeen(user.getOnlineStatus().getLastSeen())
                .build();
    }
}
