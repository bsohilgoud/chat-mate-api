package com.sohil.chatmate.service;


import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.dto.request.LoginRequestDTO;
import com.sohil.chatmate.dto.request.RegistrationRequestDTO;
import com.sohil.chatmate.enums.AuthProvider;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

public interface AuthService {

    Map<String, String> login(LoginRequestDTO loginRequestDTO);
    void logout(HttpSession session);
    UserDTO signUp(RegistrationRequestDTO registrationRequestDTO);
    Map<String, String> oauthSignIn(String email, String name, String profileUrl, String providerId, AuthProvider authProvider);

}
