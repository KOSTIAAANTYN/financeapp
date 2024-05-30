package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.config.JwtCore;
import com.financeprojectboard.app.config.JwtService;
import com.financeprojectboard.app.config.UserDetailsImpl;
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
    private final JwtService jwtService;
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

//gen jwt and sent him with cal
//    @PostMapping("/login")
//    @ResponseBody
//    public Object login(@RequestBody User user) {
//        //return message or find user
//        //email,pass
//        if (user != null && userRepository.existsByEmail(user.getEmail())) {
//            try {
//
//                Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
//                Authentication authenticatedUser = authenticationManager.authenticate(authentication);
//                SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
//                String jwt = jwtCore.generateToken(authenticatedUser);
//
//                User user1 = userRepository.findByEmail(user.getEmail());
//                UserCalendar userCalendar = user1.getUserCalendar();
//                userService.checkLongLogin(user1);
//                UserCalendarDTO userCalendarDTO = userCalendar.toDTO();
//
//                return ResponseEntity.ok(userCalendarDTO + " " + jwt);
//
//            } catch (BadCredentialsException e) {
//                System.out.println("Bad credentials provided");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//            } catch (Exception e) {
//                System.out.println("Unexpected error during authentication: " + e.getMessage());
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        } else {
//            return ResponseEntity.status(404).body("user");
//        }
//    }
//
//}

    @Operation(
            summary = "auth user and give token"
    )
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> loginToken(@RequestBody User user) {
        //return message or find user
        //email,pass
        if (user != null && userRepository.existsByEmail(user.getEmail())) {
            try {

                Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
                Authentication authenticatedUser = authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
                String jwt = jwtCore.generateToken(authenticatedUser);
                return ResponseEntity.ok(jwt);

            } catch (BadCredentialsException e) {
                System.out.println("Bad credentials provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            } catch (Exception e) {
                System.out.println("Unexpected error during authentication: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(404).body("user");
        }
    }

    @Operation(
            summary = "refresh token by unexpired token"
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
        try {
            String email = jwtCore.getEmailFromToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) jwtService.loadUserByUsername(email);
            String newToken = jwtCore.generateToken(userDetails);

            return ResponseEntity.ok(newToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
