package com.sohil.chatmate.service.impl;

import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.mapper.UserMapper;
import com.sohil.chatmate.repository.UserRepository;
import com.sohil.chatmate.security.UserPrinciple;
import com.sohil.chatmate.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //@Transactional
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        Optional<User> userByUsername = userRepository.findUserByUsername(username);

        return userByUsername.isEmpty();
    }

    @Override
    @Transactional
    public void changePassword(String userId, String newPassword) {
        User user = getUser(userId);
        user.setPassword(newPassword);

        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return findUserByUsername(email);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("!!!No user found with username: " + username));
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<User> all = userRepository.findAll();
        List<UserDTO> userDetailDTOList = new ArrayList<>();
        all.forEach( user -> {
            userDetailDTOList.add(UserMapper.toDto(user));
        });

        return userDetailDTOList;
    }

    @Override
    public User getUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("!!!No user found with userId: " + id));
    }

    @Override
    public UserPrinciple getUserPrinciple(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("!!!No user found with userId: " + id));
        return UserPrinciple.from(user);
    }

    public Optional<User> findByUserId(String receiverId) {
        return userRepository.findById(receiverId);
    }

}
