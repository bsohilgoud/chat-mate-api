package com.sohil.chatmate.controller;


import com.sohil.chatmate.dto.OnlineStatusDTO;
import com.sohil.chatmate.dto.UpdateOnlineStatusRequestDTO;
import com.sohil.chatmate.dto.UserDetailDTO;
import com.sohil.chatmate.exceptions.UserNotFoundException;
import com.sohil.chatmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/all")
    ResponseEntity<? extends Object> getAllUsers(){
        List<UserDetailDTO> allUsers = userService.getAllUsers();
        if(allUsers == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to finds users list");
        }

        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/status/{userId}")
    ResponseEntity<Object> getUserStatus(@PathVariable String userId){
        try {
            OnlineStatusDTO onlineStatus = userService.getOnlineStatus(userId);
            return ResponseEntity.ok(onlineStatus);
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/status/")
    //TIP: Don't forgot @RequestBody
    public ResponseEntity<Object> updateOnlineStatus(@RequestBody UpdateOnlineStatusRequestDTO updateOnlineStatusRequestDTO){
        try {
            userService.updateOnlineStatus(updateOnlineStatusRequestDTO);
            return ResponseEntity.ok(updateOnlineStatusRequestDTO);
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

}
