package com.financeprojectboard.app.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final JwtCore jwtCore;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String email = null;
        UserDetails userDetails = null;
        UsernamePasswordAuthenticationToken auth = null;

        try {
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                jwt = headerAuth.substring(7);
            }

            if (jwt != null) {
                if (jwtCore.isTokenExpired(jwt)) {
                    response.setStatus(401);
                    return;
                }

                email = jwtCore.getEmailFromToken(jwt);
            }

        } catch (ExpiredJwtException e) {
            response.setStatus(401);
            return;
        } catch (JwtException e) {
            System.out.println("JWT Err" + " " + e.getMessage());
            response.setStatus(401);
            return;
        } catch (Exception e) {
            System.out.println("WowErr" + " " + e.getMessage());
            response.setStatus(500);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userDetails = userDetailsService.loadUserByUsername(email);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
