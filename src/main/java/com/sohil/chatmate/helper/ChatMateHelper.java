package com.sohil.chatmate.helper;

import com.sohil.chatmate.security.UserPrinciple;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class ChatMateHelper {

    public static UserPrinciple getLoggedInUserPrinciple(){
        UserPrinciple currentLoggedInUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
             currentLoggedInUser = (UserPrinciple) authentication.getPrincipal();
        } catch (Exception e){
           throw new BadCredentialsException("Invalid User principle");
        }

        return currentLoggedInUser;
    }

    public static void storeSecurityContextInSession(HttpSession httpSession){
        // Store security context in session
        /* TIP: This is required else the JSESSIONID is not included in the response
              1. The JSESSIONID cookie is only created when a session is created
              2. Setting the Spring Security context in the session (as in your code) will create a session if it doesn't exist, causing the JSESSIONID to be added to the response
              3. When you manually set this attribute, you are telling the server to persist the authentication information in the session, which requires a session to exist
         */
        httpSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }
}
