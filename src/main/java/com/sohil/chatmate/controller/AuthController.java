package com.sohil.chatmate.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.sohil.chatmate.dto.response.ApiResponse;
import com.sohil.chatmate.dto.UserDTO;
import com.sohil.chatmate.dto.request.LoginRequestDTO;
import com.sohil.chatmate.dto.request.RegistrationRequestDTO;
import com.sohil.chatmate.enums.AuthProvider;
import com.sohil.chatmate.security.oauth.GoogleOAuthHelper;
import com.sohil.chatmate.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import static com.sohil.chatmate.helper.ChatMateHelper.storeSecurityContextInSession;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    AuthService authService;

    @Autowired
    GoogleOAuthHelper googleOAuthHelper;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody RegistrationRequestDTO registrationRequestDTO, HttpServletRequest request) {
        UserDTO responseData = authService.signUp(registrationRequestDTO);

        return ApiResponse.success(HttpStatus.CREATED.value(), responseData, "User registration successful", request.getRequestURI());
//        // TIP: Lombok builder is by default private package so we need to use the static builder() instead of new ApiResponseBuilder
//        return ResponseEntity.ok(
//                ApiResponse.<UserDTO>builder()
//                        .status(HttpStatus.CREATED.value())
//                        .data(responseData)
//                        .path(request.getRequestURI())
//                        .message("User registration successful")
//                        .build()
//        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpSession session, HttpServletRequest request) {
        Map<String, String> responseData = authService.login(loginRequestDTO);
        return ApiResponse.success(HttpStatus.OK.value(), responseData, "User login successful", request.getRequestURI());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpSession session, HttpServletRequest request) {
        authService.logout(session);

        return ApiResponse.success(HttpStatus.OK.value(), null, "User logout successful", request.getRequestURI());
    }


    @PostMapping("/google")
    public ResponseEntity<ApiResponse<UserDTO>> oauthLoginWithGoogleAuthCode(@RequestBody GoogleOAuthHelper.GoogleAuthCodeDTO googleAuthCodeDTO, HttpSession session, HttpServletRequest request) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = googleOAuthHelper.oauthLoginWithAuthCode(googleAuthCodeDTO.authCode());
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String userGoogleId = payload.getSubject();
        String profileUrl = (String) payload.get("picture");

        UserDTO responseData = authService.oauthSignIn(email, name, profileUrl, userGoogleId, AuthProvider.GOOGLE);
        storeSecurityContextInSession(session);

        return ApiResponse.success(HttpStatus.OK.value(), responseData, "User login successful", request.getRequestURI());
    }

    // NOT Recommended Approach
    @PostMapping("/google/tokenId")
    public ResponseEntity<ApiResponse<UserDTO>> oauthLoginWithGoogle(@RequestBody GoogleOAuthHelper.GoogleTokenDTO googleTokenDTO, HttpSession session, HttpServletRequest request) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = googleOAuthHelper.oauthLoginWithTokenID(googleTokenDTO.googleToken());
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String userGoogleId = payload.getSubject();
        String profileUrl = (String) payload.get("picture");
        UserDTO responseData = authService.oauthSignIn(email, name, profileUrl, userGoogleId, AuthProvider.GOOGLE);
        storeSecurityContextInSession(session);

        return ApiResponse.success(HttpStatus.OK.value(), responseData, "User login successful", request.getRequestURI());
    }
}
