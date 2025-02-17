package com.sohil.chatmate.controller;


import com.sohil.chatmate.dto.UserDetailDTO;
import com.sohil.chatmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    List<UserDetailDTO> getUsersList(){
        return userService.getAllUsers();
    }

}
