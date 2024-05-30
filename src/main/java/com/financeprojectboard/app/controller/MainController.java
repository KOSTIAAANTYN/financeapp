package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.DTO.UserCalendarDTO;
import com.financeprojectboard.app.DTO.UserHistoryDTO;
import com.financeprojectboard.app.config.UserDetailsImpl;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserCalendar;
import com.financeprojectboard.app.repositories.UserRepository;
import com.financeprojectboard.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/secured")
public class MainController {
    private final UserService userService;
    private final UserRepository userRepository;

    //give cal by token
    @PostMapping("/loginAndCalendar")
    @ResponseBody
    public Object loginAndCalendar(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByEmail(userDetails.getEmail());
        UserCalendar userCalendar = user.getUserCalendar();
        userService.checkLongLogin(user);

        return userCalendar.toDTO();
    }

    @PostMapping("/updateCalendar")
    public ResponseEntity<?> updateCalendar(@RequestBody UserCalendarDTO userCalendarDTO
            , @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.updateCalendar(userCalendarDTO));
    }


    @PostMapping("/addToHistory")
    public ResponseEntity<String> addToHistory(@RequestBody UserHistoryDTO userHistoryDTO
            , @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        userService.addToHistory(userHistoryDTO);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/removeOneHistoryElem")
    public ResponseEntity<String> removeOneHistoryElem(@RequestBody Map<String, Long> requestBody
            , @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = requestBody.get("userId");
        Long index = requestBody.get("index");
        userService.removeOneHistoryElem(userId, index);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/clearHistory")
    public ResponseEntity<String> clearHistory(@RequestBody Map<String, Long> requestBody
            , @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = requestBody.get("userId");
        userService.clearHistory(userId);
        return ResponseEntity.ok().body("ok");
    }

    //<<<pass+email
    //email-code>>>front
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody User user
            , @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.ok(userService.emailAuth(user.getEmail()));
        } else {
            return ResponseEntity.status(404).body("User doesn't exist");
        }
    }

    //saved changed pass
    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestBody User user
            ,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.savePass(user));
    }

    //200ok 400bad
    @PostMapping("/changeName")
    public ResponseEntity<String> changeName(@RequestBody User user
            ,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        if (userService.changeName(user.getId(), user.getUsername())) {
            return ResponseEntity.ok().body("ok");
        } else {
            return ResponseEntity.status(400).body("bad");
        }
    }

    @PostMapping("/deleteAccount")
    public ResponseEntity<String> deleteAccount(@RequestBody User user
            ,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        if (userService.delUser(user.getId())) {
            return ResponseEntity.ok().body("User deleted");
        } else return ResponseEntity.status(400).body("bad");
    }

    @PostMapping("/contact")
    public ResponseEntity<String> contactUs(@RequestBody Map<String, String> requestBody
            ,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        String email = requestBody.get("email");
        String question = requestBody.get("question");
        if (email != null && question != null && userService.contactUs(email, question)) {
            return ResponseEntity.ok().body("");
        } else return ResponseEntity.status(400).body("");
    }
}
