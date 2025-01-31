package com.sohil.chatmate.service;

import com.sohil.chatmate.dto.UserRegistrationDTO;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public Optional<User> findByUserId(String receiverId) {
        return userRepository.findById(receiverId);
    }
}
