package com.financeprojectboard.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import java.util.Date;

@Component
public class JwtCore {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.jwtExpirationMs}")
    private long jwtExpirationMs;
    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    public String generateAccessToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateToken(userPrincipal.getEmail(), "access", jwtExpirationMs);
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateToken(userPrincipal.getEmail(), "refresh", refreshExpirationMs);
    }

    private String generateToken(String email, String tokenType, long expirationMs) {
        return Jwts.builder()
                .setSubject(email)
                .claim("tokenType", tokenType)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationMs))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String getTokenType(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("tokenType", String.class);
    }

    public String refreshAccessToken(String refreshToken) {
        if (validateJwtToken(refreshToken)) {
            String tokenType = getTokenType(refreshToken);
            if (!"refresh".equals(tokenType)) {
                throw new RuntimeException("Invalid Token Type for Refresh");
            }
            String email = getEmailFromToken(refreshToken);
            return generateToken(email, "access", jwtExpirationMs);
        }
        throw new RuntimeException("Invalid Refresh Token");
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}


