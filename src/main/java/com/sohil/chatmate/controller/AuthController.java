package com.sohil.chatmate.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.sohil.chatmate.dto.UserLoginDTO;
import com.sohil.chatmate.dto.UserRegistrationDTO;
import com.sohil.chatmate.enums.AuthProvider;
import com.sohil.chatmate.exceptions.UsernameAlreadyExistsException;
import com.sohil.chatmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    UserService userService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;



    AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/oauth/google")
    public ResponseEntity<? extends Object> oauthLoginWithGoogle(@RequestBody GoogleTokenDTO googleTokenDTO) throws GeneralSecurityException, IOException {
        String googleToken = googleTokenDTO.googleToken();
        GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(googleToken);
        if(googleIdToken != null){
            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String userGoogleId = payload.getSubject();
            String pictureUrl = (String) payload.get("picture");

            Map<String, Object> response = userService.oauthSignIn(email, name, userGoogleId, AuthProvider.GOOGLE);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.internalServerError().build();
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


    @PostMapping("/google")
    public ResponseEntity<? extends Object> oauthLoginWithGoogleAuthCode(@RequestBody GoogleAuthCodeDTO googleAuthCodeDTO, HttpSession session) throws GeneralSecurityException, IOException {
        // Exchange auth code for tokens
        System.out.println("googleAuthCodeDTO = " + googleAuthCodeDTO);
        String code = googleAuthCodeDTO.authCode();
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();

        System.out.println("GOOGLE_CLIENT_ID = " + googleClientId);
        System.out.println("googleClientSecret = " + googleClientSecret);
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                transport,
                jsonFactory,
                "https://oauth2.googleapis.com/token",
                googleClientId,
                googleClientSecret,
                code,
                "http://localhost:5173"
        ).execute();

        String idToken = tokenResponse.getIdToken();
        System.out.println("idToken = " + idToken);
        GoogleIdToken googleIdToken = GoogleIdToken.parse(jsonFactory, idToken);
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String userGoogleId = payload.getSubject();
        String pictureUrl = (String) payload.get("picture");

        Map<String, Object> response = userService.oauthSignIn(email, name, userGoogleId, AuthProvider.GOOGLE);

        /* TIP: This is required else the JSESSIONID is not included in the response
              1. The JSESSIONID cookie is only created when a session is created
              2. Setting the Spring Security context in the session (as in your code) will create a session if it doesn't exist, causing the JSESSIONID to be added to the response
              3. When you manually set this attribute, you are telling the server to persist the authentication information in the session, which requires a session to exist
         */
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        return ResponseEntity.ok(response);
    }


    public record GoogleTokenDTO(String googleToken){}
    public record GoogleAuthCodeDTO(String authCode){}
}
