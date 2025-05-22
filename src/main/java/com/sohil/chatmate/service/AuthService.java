package com.sohil.chatmate.service;


import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.dto.UserLoginDTO;
import com.sohil.chatmate.dto.UserRegistrationDTO;
import com.sohil.chatmate.enums.AuthProvider;
import jakarta.servlet.http.HttpSession;

public interface AuthService {

    UserDTO login(UserLoginDTO userLoginDTO);
    void logout(HttpSession session);
    UserDTO signUp(UserRegistrationDTO userRegistrationDTO);
    UserDTO oauthSignIn(String email, String name, String profileUrl, String providerId, AuthProvider authProvider);

}
