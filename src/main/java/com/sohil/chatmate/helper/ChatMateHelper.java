package com.sohil.chatmate.helper;

import com.sohil.chatmate.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public  class ChatMateHelper {

    public static User getLoggedInUser(){
        User currentLoggedInUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
             currentLoggedInUser = (User) authentication.getPrincipal();
        } catch (Exception e){
            System.out.println(e);
        }
        return currentLoggedInUser;
    }
}
