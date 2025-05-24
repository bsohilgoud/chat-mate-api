package com.sohil.chatmate.service;


import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.security.UserPrinciple;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User findUserByEmail(String email);

    User findUserByUsername(String username);

    List<UserDTO> findAllUsers();

    User getUser(String id);

    UserPrinciple getUserPrinciple(String id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String id);

    boolean existsByUsername(String username);

    void changePassword(String userId, String newPassword);
}