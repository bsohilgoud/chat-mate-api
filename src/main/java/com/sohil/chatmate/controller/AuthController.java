package com.sohil.chatmate.controller;

import com.sohil.chatmate.dto.UserLoginDTO;
import com.sohil.chatmate.dto.UserRegistrationDTO;
import com.sohil.chatmate.exceptions.UsernameAlreadyExistsException;
import com.sohil.chatmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    UserService userService;

    AuthController(UserService userService) {
        this.userService = userService;
    }

    /* Request URL
        POST http://localhost:8080/user/register
        body : {
            "username": "sai@123",
            "password" : "1234",
            "displayName" : "Sai"
        }
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        try {
            Map<String, Object> response = userService.registerUser(userRegistrationDTO);
            return ResponseEntity.ok(response);
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginDTO userLoginDTO, HttpSession session) {

        try {
            Map<String, Object> response = userService.authenticateUser(
                    userLoginDTO.username(), userLoginDTO.password()
            );

            // Store security context in session
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpSession session) {
        try {
            Map<String, Object> response = userService.logoutUser(session);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
