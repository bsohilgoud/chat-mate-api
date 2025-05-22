package com.sohil.chatmate.mapper;

import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.entity.User;

public class UserMapper {

    public static UserDTO toDto(User user){
        return new UserDTO(
                user.getUserID(),
                user.getEmail(),
                user.getUsername(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getProfileUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
