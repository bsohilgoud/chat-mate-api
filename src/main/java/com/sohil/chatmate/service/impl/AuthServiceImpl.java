package com.sohil.chatmate.service.impl;

import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.dto.request.LoginRequestDTO;
import com.sohil.chatmate.dto.request.RegistrationRequestDTO;
import com.sohil.chatmate.entity.OnlineStatus;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.enums.AuthProvider;
import com.sohil.chatmate.enums.NotificationType;
import com.sohil.chatmate.exceptions.UsernameAlreadyExistsException;
import com.sohil.chatmate.helper.ChatMateHelper;
import com.sohil.chatmate.security.jwt.JWTHelper;
import com.sohil.chatmate.helper.NotificationService;
import com.sohil.chatmate.mapper.UserMapper;
import com.sohil.chatmate.security.UserPrinciple;
import com.sohil.chatmate.service.AuthService;
import com.sohil.chatmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    OnlineStatusServiceImpl onlineStatusService;
    NotificationService notificationService;
    UserService userService;

    @Autowired
    JWTHelper jwtHelper;

    public AuthServiceImpl(OnlineStatusServiceImpl onlineStatusService, NotificationService notificationService, UserService userService) {
        this.onlineStatusService = onlineStatusService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public Map<String, String> login(LoginRequestDTO loginRequestDTO) {
        String username = loginRequestDTO.username();
        String password = loginRequestDTO.password();

        User user = userService.findUserByUsername(username);

        if (!isValidUser(user, password)) {
            throw new BadCredentialsException("!!!Invalid Credentials");
        }

        setAuthenticationInSecurityContext(user);

        String userID = user.getUserId();
        onlineStatusService.updateOnlineStatus(userID, OnlineStatus.StatusType.ONLINE);

        notificationService.notification(NotificationType.USER_ONLINE)
                .fromUser(userID)
                .send();

        HashMap<String, Object> claims = new HashMap(3);
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFullName());

        String jwtToken = jwtHelper.generateAccessToken(userID, claims);

        return Map.of("token", jwtToken);
    }

    @Override
    public void logout(HttpSession session) {
        UserPrinciple loggedInUser = ChatMateHelper.getLoggedInUserPrinciple();

        String userID = loggedInUser.getUserID();
        SecurityContextHolder.clearContext();
        onlineStatusService.updateOnlineStatus(userID, OnlineStatus.StatusType.OFFLINE);

        Map<String, String> response = new HashMap<>();
        response.put("userId", userID);
        session.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        notificationService.notification(NotificationType.USER_OFFLINE)
                .fromUser(userID)
                .send();
    }

    @Override
    public UserDTO signUp(RegistrationRequestDTO registrationRequestDTO) {
        String username = registrationRequestDTO.username();
        if(userService.existsByUsername(username)){
            throw new UsernameAlreadyExistsException(username + ": username already exists !!");
        }

        LocalDateTime time = LocalDateTime.now();
        User user = User.builder()
                .username(registrationRequestDTO.username())
                .email(registrationRequestDTO.username())
                .password(registrationRequestDTO.password())
                .fullName(registrationRequestDTO.displayName())
                .authProvider(AuthProvider.LOCAL)
                .createdAt(time)
                .updatedAt(time)
                .build();

        User newUser = userService.createUser(user);
        onlineStatusService.createOnlineStatus(newUser, OnlineStatus.StatusType.ONLINE);

        return UserMapper.toDto(newUser);
    }


    public UserDTO oauthSignIn(String email, String name, String profileUrl, String providerId, AuthProvider authProvider) {
        User user;
        if(userService.existsByUsername(email)){
            User newUser = User.builder()
                    .username(email)
                    .fullName(name)
                    .profileUrl(profileUrl)
                    .providerId(providerId)
                    .authProvider(authProvider)
                    .build();

            user = userService.createUser(newUser);
            onlineStatusService.createOnlineStatus(user, OnlineStatus.StatusType.ONLINE);
        } else {
            user = userService.findUserByUsername(email);
        }

        setAuthenticationInSecurityContext(user);

        String userID = user.getUserId();
        onlineStatusService.updateOnlineStatus(userID, OnlineStatus.StatusType.ONLINE);

        notificationService.notification(NotificationType.USER_ONLINE)
                .fromUser(userID)
                .send();

        return UserMapper.toDto(user);
    }


    private boolean isValidUser(User user, String password) {
        return user != null && password.equals(user.getPassword());
    }

    private void setAuthenticationInSecurityContext(User user){
        // Create authentication token
        UserPrinciple userPrinciple = UserPrinciple.from(user);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrinciple, null, authorities
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("User logged: " + user.getFullName());
//        System.out.println("===================================================================");
//        System.out.println("Added logged in user to SecurityContextHolder :  " + authentication);
//        System.out.println("===================================================================");
    }
}
