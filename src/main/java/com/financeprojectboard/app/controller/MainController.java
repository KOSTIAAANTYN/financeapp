package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.DTO.UserCalendarDTO;
import com.financeprojectboard.app.DTO.UserHistoryDTO;
import com.financeprojectboard.app.config.JwtCore;
import com.financeprojectboard.app.config.UserDetailsImpl;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserCalendar;
import com.financeprojectboard.app.repositories.UserRepository;
import com.financeprojectboard.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "main_methods")
@RequestMapping("/secured")
public class MainController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtCore jwtCore;

    @Operation(
            summary = "gives calendar by token"
    )
    //give cal by token
    @PostMapping("/loginAndCalendar")
    @ResponseBody
    public ResponseEntity<Object> loginAndCalendar(@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }

        User user = userRepository.findByEmail(jwtCore.getEmailFromToken(token.substring(7)));
        UserCalendar userCalendar = user.getUserCalendar();
        userService.checkLongLogin(user);
        return ResponseEntity.ok(userCalendar.toDTO());
    }

    private ResponseEntity<Object> checkToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String accessToken = token.substring(7);
            if ("access".equals(jwtCore.getTokenType(accessToken))) {
                return null;
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token type");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token not provided");
        }
    }

    @Operation(
            summary = "receive user and jwt token, then update his calendar",
            description = "if no id + message , id + message+changes=update, id_base not found = delete"
    )
    @PostMapping("/updateCalendar")
    public ResponseEntity<?> updateCalendar(@RequestBody UserCalendarDTO userCalendarDTO
            , @RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        return ResponseEntity.ok(userService.updateCalendar(userCalendarDTO));
    }

    @Operation(
            summary = "receive user history and jwt token, then add him",
            description = "add to history or throw err User not found with id:"
    )
    @PostMapping("/addToHistory")
    public ResponseEntity<?> addToHistory(@RequestBody UserHistoryDTO userHistoryDTO
            , @RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        userService.addToHistory(userHistoryDTO);
        return ResponseEntity.ok().body("ok");
    }

    @Operation(
            summary = "receive user id, index of history in list,and jwt token, then delete him",
            description = "remove history by user id and index or throw err User not found with id:"
    )
    @PostMapping("/removeOneHistoryElem")
    public ResponseEntity<?> removeOneHistoryElem(@RequestBody Map<String, Long> requestBody
            , @RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        Long userId = requestBody.get("userId");
        Long index = requestBody.get("index");
        userService.removeOneHistoryElem(userId, index);
        return ResponseEntity.ok().body("ok");
    }

    @Operation(
            summary = "receive user id, index of history in list and jwt token, then delete him",
            description = "remove history by user id and index or throw err User not found with id:"
    )
    @PostMapping("/clearHistory")
    public ResponseEntity<?> clearHistory(@RequestBody Map<String, Long> requestBody
            , @RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        Long userId = requestBody.get("userId");
        userService.clearHistory(userId);
        return ResponseEntity.ok().body("ok");
    }

    @Operation(
            summary = "<<<pass+email+token/email-code>>>front"
    )
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody User user
            , @RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.ok(userService.emailAuth(user.getEmail()));
        } else {
            return ResponseEntity.status(404).body("User doesn't exist");
        }
    }

    @Operation(
            summary = "saved changed pass"
    )
    //saved changed pass
    @PostMapping("/savePassword")
    public ResponseEntity<?> savePassword(@RequestBody User user
            ,@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        return ResponseEntity.ok(userService.savePass(user));
    }

    @Operation(
            summary = "change name",
            description = "200ok 400bad"
    )
    //200ok 400bad
    @PostMapping("/changeName")
    public ResponseEntity<?> changeName(@RequestBody User user
            ,@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        if (userService.changeName(user.getId(), user.getUsername())) {
            return ResponseEntity.ok().body("ok");
        } else {
            return ResponseEntity.status(400).body("bad");
        }
    }

    @Operation(
            summary = "delete account"
    )
    @PostMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestBody User user
            ,@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        if (userService.delUser(user.getId())) {
            return ResponseEntity.ok().body("User deleted");
        } else return ResponseEntity.status(400).body("bad");
    }

    @Operation(
            summary = "contact with devs by email"
    )
    @PostMapping("/contact")
    public ResponseEntity<?> contactUs(@RequestBody Map<String, String> requestBody
            ,@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenCheckResult = checkToken(token);
        if (tokenCheckResult != null) {
            return tokenCheckResult;
        }
        String email = requestBody.get("email");
        String question = requestBody.get("question");
        if (email != null && question != null && userService.contactUs(email, question)) {
            return ResponseEntity.ok().body("");
        } else return ResponseEntity.status(400).body("");
    }
}
