package com.sohil.chatmate.security;

import com.sohil.chatmate.entity.User;
import lombok.Getter;

import java.util.List;

@Getter
public class UserPrinciple {
    private final String userID;
    private final String username;
    private final String email;
    private final List<String> roles;
    private final boolean enabled;

    private UserPrinciple(String userId, String username, String email, List<String> roles, boolean enabled) {
        this.userID = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.enabled = enabled;
    }

    public static UserPrinciple from(User user){
        return new UserPrinciple(user.getUserId(), user.getUsername(), user.getEmail(), null, true);
    }
}
