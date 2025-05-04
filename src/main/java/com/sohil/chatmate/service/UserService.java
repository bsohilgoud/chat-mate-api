package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.OnlineStatusDTO;
import com.sohil.chatmate.dto.UpdateOnlineStatusRequestDTO;
import com.sohil.chatmate.dto.UserDetailDTO;
import com.sohil.chatmate.dto.UserRegistrationDTO;
import com.sohil.chatmate.entity.OnlineStatus;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.enums.AuthProvider;
import com.sohil.chatmate.exceptions.UserNotFoundException;
import com.sohil.chatmate.exceptions.UsernameAlreadyExistsException;
import com.sohil.chatmate.repository.OnlineStatusRepository;
import com.sohil.chatmate.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    UserRepository userRepository;
    OnlineStatusRepository onlineStatusRepository;

    public UserService(UserRepository userRepository, OnlineStatusRepository onlineStatusRepository) {
        this.userRepository = userRepository;
        this.onlineStatusRepository = onlineStatusRepository;
    }

    //    @Transactional
    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        User user = User.builder()
                .username(userRegistrationDTO.username())
                .password(userRegistrationDTO.password())
                .displayName(userRegistrationDTO.displayName())
                .build();

        userRepository.save(user);

    }

    private boolean isValidUser(User user, String password) {
        return user != null && password.equals(user.getPassword());
    }

    private Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public Optional<User> findByUserId(String receiverId) {
        return userRepository.findById(receiverId);
    }

    public List<UserDetailDTO> getAllUsers() {
        List<User> all = userRepository.findAll();
        List<UserDetailDTO> userDetailDTOList = new ArrayList<>();
        all.forEach( user -> {
            userDetailDTOList.add(new UserDetailDTO(user.getUserID(), user.getUsername(), user.getDisplayName()));
        });

        return userDetailDTOList;
    }

    private boolean checkIFUserExists(String username){
        Optional<User> userByUsername = findUserByUsername(username);
        return userByUsername.isPresent();
    }

    /**
     * Creates new user if not exists
     * @param userRegistrationDTO
     * @return
     */
    public Map<String, Object> registerUser(UserRegistrationDTO userRegistrationDTO) {
        String username = userRegistrationDTO.username();
        if(checkIFUserExists(username)){
            throw new UsernameAlreadyExistsException(username + ": username already exists !!");
        }

        User user = User.builder()
                .username(userRegistrationDTO.username())
                .password(userRegistrationDTO.password())
                .displayName(userRegistrationDTO.displayName())
                .authProvider(AuthProvider.LOCAL)
                .build();

        User newUser = userRepository.save(user);

        // Adding online_status entry for the given user
        OnlineStatus userOnlineStatus = new OnlineStatus(newUser.getUserID(), OnlineStatus.StatusType.ONLINE, LocalDateTime.now());
        onlineStatusRepository.save(userOnlineStatus);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully registered the user: " + username);
        response.put("userId", newUser.getUserID()); // Assuming User has a getUserID() method
        return response;
    }

    /**
     * Authenticates the users by comparing the password
     * @param username
     * @param password
     * @return
     */
    public Map<String, Object> authenticateUser(String username, String password) {

        Optional<User> optionalUser = findUserByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("No user found!!!");
        }

        User user = optionalUser.get();
        if (!isValidUser(user, password)) {
            throw new BadCredentialsException("Invalid Credentials!!!");
        }

        // Create authentication token
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, authorities
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("===============================");
        System.out.println("Added logged in user to SecurityContextHolder :  " + authentication);
        System.out.println("===============================");

        updateOnlineStatus(user.getUserID(), OnlineStatus.StatusType.ONLINE);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully logged in");
        response.put("userId", user.getUserID()); // Assuming User has a getUserID() method
        return response;
    }

    @Transactional
    private void updateOnlineStatus(String userId, OnlineStatus.StatusType statusType) {
        OnlineStatus onlineStatus = onlineStatusRepository.getReferenceById(userId);
        onlineStatus.setStatus(statusType);
        onlineStatus.setLastSeen(LocalDateTime.now());
        onlineStatusRepository.save(onlineStatus);
    }

    // TIP: Use DTO's else we are getting Serialization exception
    // ERROR : The error you're encountering, InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor, typically arises when attempting to serialize a Hibernate proxy object that is in a lazy-loaded state. This situation often occurs in Spring applications using JPA with Hibernate when the entity relationships are set to FetchType.LAZY, leading to proxy objects that Jackson cannot serialize directly.
    public OnlineStatusDTO getOnlineStatus(String userId) {
        // TIP: When using getReferenceById in JPA, it’s important to understand its behavior. This method returns a reference to the entity with the given identifier, but it does not immediately hit the database to check if the entity exists. Instead, it creates a proxy object that will be initialized when you access any of its properties.
        if(!onlineStatusRepository.existsById(userId)){
            throw new UserNotFoundException("Unable to find user with id: " + userId);
        }

        OnlineStatus onlineStatus = onlineStatusRepository.getReferenceById(userId);
        return new OnlineStatusDTO(onlineStatus.getUserId(), onlineStatus.getLastSeen(), onlineStatus.getStatus().toString());
    }

    public void updateOnlineStatus(UpdateOnlineStatusRequestDTO updateOnlineStatusRequestDTO) {
        String userId = updateOnlineStatusRequestDTO.userId();
        OnlineStatus.StatusType status = updateOnlineStatusRequestDTO.status();
        if(!onlineStatusRepository.existsById(userId)){
            throw new UserNotFoundException("Unable to find user with id: " + userId);
        }

        onlineStatusRepository.updateStatus(userId, status);
    }
}
