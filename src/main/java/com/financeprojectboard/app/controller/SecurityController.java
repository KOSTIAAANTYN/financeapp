package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.config.JwtCore;
import com.financeprojectboard.app.config.JwtService;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.repositories.UserRepository;
import com.financeprojectboard.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@Tag(name = "auth_methods")
@RequestMapping("/auth")
public class SecurityController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    //http://localhost:8080/swagger-ui/index.html#/

    @Operation(
            summary = "receive user and save him"
    )
    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUserC(user));
    }

    @Operation(
            summary = "receive user email, send code to front"
    )
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody User user) {
        //return message or mail code
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(204).body("isExist");
        } else {
            return ResponseEntity.ok(userService.emailAuth(user.getEmail()));
        }
    }

    @Operation(
            summary = "auth user and give tokens"
    )
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateAccessToken(authentication);
        String refreshToken = jwtCore.generateRefreshToken(authentication);
        return ResponseEntity.ok(jwt + ";" + refreshToken);
    }

    @Operation(
            summary = "refresh access token by refresh token"
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refToken) {
        if (refToken != null && refToken.startsWith("Bearer ")) {
            refToken = refToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
        String newToken = jwtCore.refreshAccessToken(refToken);
        return ResponseEntity.ok(newToken + ";" + refToken);
    }
}

