package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.config.JwtCore;
import com.financeprojectboard.app.config.JwtService;
import com.financeprojectboard.app.model.TempUser;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.repositories.UserRepository;
import com.financeprojectboard.app.service.TempUserService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

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
    private final PasswordEncoder passwordEncoder;
    private final TempUserService tempUserService;
    //http://localhost:8080/swagger-ui/index.html#/

        @Operation(summary = "receive user email, send code to front")
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(204).body("isExist");
        }
        String code = userService.emailAuth(user.getEmail());
        tempUserService.saveTempUser(user.getEmail()
                , user.getUsername()
                , passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(code);
    }



    @Operation(summary = "receive user and save him after verification")
    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody User userReq) {

        TempUser tempUser = tempUserService.getTempUser(userReq.getEmail());

        if (tempUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("expired");
        }

        userService.saveUserC(tempUser.getEmail(),
                tempUser.getUsername(),
                tempUser.getPasswordHash());

        tempUserService.deleteTempUser(userReq.getEmail());

        return ResponseEntity.ok("User saved");
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

    @Operation(
            summary = "<<<pass+email+token/email-code>>>front"
    )
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.ok(userService.emailAuth(user.getEmail()));
        } else {
            return ResponseEntity.status(404).body("User doesn't exist");
        }
    }


    @Operation(
            summary = "saved changed pass"
    )
    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestBody User user) {
        return ResponseEntity.ok(userService.savePass(user));
    }
}

