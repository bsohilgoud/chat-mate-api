package com.sohil.chatmate.dto;

/*{"username": "sai@123" , password : "1234", "displayName" : "Sai"} */
public record UserRegistrationDTO(String username, String password, String displayName) {
}