package com.sohil.chatmate.controller;

import com.sohil.chatmate.dto.UserLoginDTO;
import com.sohil.chatmate.dto.UserRegistrationDTO;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    UserController(UserService userService) {
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
    public String register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        userService.createUser(userRegistrationDTO);
        return "Successfully Register the user: " + userRegistrationDTO.username();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLoginDTO, HttpSession session) {
        Optional<User> optionalUser = userService.findUserByUsername(userLoginDTO.username());
        if (optionalUser.isEmpty())
            return new ResponseEntity<>("No user found!!!", HttpStatusCode.valueOf(401));

        User user = optionalUser.get();
        boolean isValidUser = userLoginDTO.password().equals(user.getPassword());

        if (!isValidUser) {
            return new ResponseEntity<>("Invalid Credentials!!!", HttpStatusCode.valueOf(403));
        }

        // Create authentication token
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null, // Don't include password in token
                authorities
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create new session and store security context
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        return new ResponseEntity<>("Successfully logged In", HttpStatusCode.valueOf(200));
    }

    @GetMapping("/hello")
    public String greetings() {
        return "Hi Mate!!! Welcome to chatmate";
    }

}
