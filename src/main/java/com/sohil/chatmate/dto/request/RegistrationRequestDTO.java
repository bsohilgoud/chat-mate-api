package com.sohil.chatmate.dto.request;

/*{"username": "sai@123" , password : "1234", "displayName" : "Sai"} */
public record RegistrationRequestDTO(String username, String password, String displayName) {
}