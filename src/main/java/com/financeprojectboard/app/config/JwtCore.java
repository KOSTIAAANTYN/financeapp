package com.financeprojectboard.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.net.Authenticator;
import java.util.Date;

@Component
public class JwtCore {
    @Value("${app.secret}")
    private String secret;
    @Value("${jwt.lifetime}")
    private int lifetime;

    public String generateToken(Authentication authenticator) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticator.getPrincipal();
        return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + lifetime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
