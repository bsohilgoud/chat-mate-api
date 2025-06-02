package com.sohil.chatmate.security.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.sohil.chatmate.exceptions.ChatMateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleOAuthHelper {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    HttpTransport transport = new NetHttpTransport();
    JsonFactory jsonFactory = new GsonFactory();


    public GoogleIdToken.Payload oauthLoginWithAuthCode(String authCode) throws IOException {

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                transport,
                jsonFactory,
                "https://oauth2.googleapis.com/token",
                googleClientId,
                googleClientSecret,
                authCode,
                "http://localhost:5173"
        ).execute();

        String idToken = tokenResponse.getIdToken();
        GoogleIdToken googleIdToken = GoogleIdToken.parse(jsonFactory, idToken);
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        return payload;
    }

    // TIP: Not recommended
    public GoogleIdToken.Payload oauthLoginWithTokenID(String googleToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(googleToken);
        if (googleIdToken == null)
            throw new ChatMateException("Failed to login with google token id");

        return googleIdToken.getPayload();
    }

    public record GoogleTokenDTO(String googleToken) {
    }

    public record GoogleAuthCodeDTO(String authCode) {
    }
}
