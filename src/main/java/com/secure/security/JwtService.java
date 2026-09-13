package com.secure.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;
	
	
	private SecretKey getSecretKey() {
	    return Keys.hmacShaKeyFor(secret.getBytes());
	}
    
    public String generateToken(String username,String role) {

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("type", "ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 1000L * 60 * 15 
                ))       
                .signWith(getSecretKey())
                .compact();
    }


    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .subject(username)
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7
                ))
                .signWith(getSecretKey())
                .compact();
    }


    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {

        try {
            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && isTokenValid(token);

        } catch (Exception e) {
            return false;
        }
    }


    public boolean isTokenValid(String token) {

        try {

            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }


    public boolean isAccessToken(String token) {
        try {
            String type = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("type", String.class);

            return "ACCESS".equals(type);

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            String type = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("type", String.class);

            return "REFRESH".equals(type);

        } catch (Exception e) {
            return false;
        }
    }
}