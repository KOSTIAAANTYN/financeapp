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

    @Value("${jwt.lifetime}")
    private int lifetime;

    public String generateToken(Authentication authenticator) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticator.getPrincipal();
        return Jwts.builder().setSubject(userDetails.getEmail()).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + lifetime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenExpired(String jwt) {
        try {
            Jwts.parser().setSigningKey(secret).build().parseClaimsJws(jwt);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            throw e;
        }
    }
}


