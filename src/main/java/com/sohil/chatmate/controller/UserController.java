package com.sohil.chatmate.controller;


import com.sohil.chatmate.dto.OnlineStatusDTO;
import com.sohil.chatmate.dto.request.UpdateOnlineStatusRequestDTO;
import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.dto.response.ApiResponse;
import com.sohil.chatmate.exceptions.UserNotFoundException;
import com.sohil.chatmate.service.OnlineStatusService;
import com.sohil.chatmate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    OnlineStatusService onlineStatusService;

    @GetMapping("/")
    ResponseEntity<ApiResponse<List<UserDTO>>> getUsersList(HttpServletRequest httpServletRequest){
        List<UserDTO> allUsers = userService.findAllUsers();
        return ApiResponse.success(HttpStatus.OK.value(), allUsers, httpServletRequest.getRequestURI());
    }

    @GetMapping("/{userId}")
    ResponseEntity<ApiResponse<UserDTO>> getUsersList(@PathVariable String userId, HttpServletRequest httpServletRequest){
        UserDTO userDTO = userService.findUserById(userId);
        return ApiResponse.success(HttpStatus.OK.value(), userDTO, httpServletRequest.getRequestURI());
    }

    @GetMapping("/me")
    ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(HttpServletRequest request){
        UserDTO currentUser = userService.getCurrentUser();
       return ApiResponse.success(HttpStatus.OK.value(), currentUser, "Current logged in user", request.getRequestURI());
    }

    @GetMapping("/all")
    ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(HttpServletRequest httpServletRequest){
        List<UserDTO> allUsers = userService.findAllUsers();
        return ApiResponse.success(HttpStatus.OK.value(), allUsers, httpServletRequest.getRequestURI());
    }

    @GetMapping("/status/{userId}")
    ResponseEntity<Object> getUserStatus(@PathVariable String userId){
        try {
            OnlineStatusDTO onlineStatus = onlineStatusService.getOnlineStatus(userId);
            return ResponseEntity.ok(onlineStatus);
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/status/")
    //TIP: Don't forgot @RequestBody
    public ResponseEntity<Object> updateOnlineStatus(@RequestBody UpdateOnlineStatusRequestDTO updateOnlineStatusRequestDTO){
        try {
            onlineStatusService.updateOnlineStatus(updateOnlineStatusRequestDTO);
            return ResponseEntity.ok(updateOnlineStatusRequestDTO);
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

}
