package com.sohil.chatmate.security.jwt;

import com.sohil.chatmate.security.UserPrinciple;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

@Component
public class JWTHelper {

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    public String generateAccessToken(String userId){
        long tokenExpirationTime = 86400000L;
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateAccessToken(String userId, HashMap<String, Object> customClaims){
            long tokenExpirationTime = 86400000L;
            return Jwts.builder()
                    .subject(userId)
                    .claims(customClaims)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
                    .signWith(getSignInKey())
                    .compact();
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    private String extractUserId(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUserId(String token){
        return extractUserId(token);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserPrinciple userPrinciple) {
        final String userId = extractUserId(token);
        return (userId.equals(userPrinciple.getUserID()) && !isTokenExpired(token));
    }

    private SecretKey getSignInKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
