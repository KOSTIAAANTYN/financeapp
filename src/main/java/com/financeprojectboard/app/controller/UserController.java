package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.DTO.UserCalendarDTO;
import com.financeprojectboard.app.DTO.UserHistoryDTO;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserCalendar;
import com.financeprojectboard.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    @PostMapping("/addToHistory")
    public ResponseEntity<String> addToHistory(@RequestBody UserHistoryDTO userHistoryDTO) {
        userService.addToHistory(userHistoryDTO);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/removeOneHistoryElem")
    public ResponseEntity<String> removeOneHistoryElem(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId");
        Long index = requestBody.get("index");
        userService.removeOneHistoryElem(userId, index);
        return ResponseEntity.ok().body("ok");
    }
    @PostMapping("/clearHistory")
    public ResponseEntity<String> clearHistory(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId");
        userService.clearHistory(userId);
        return ResponseEntity.ok().body("ok");
    }


    @PostMapping("/updateCalendar")
    public ResponseEntity<String> updateCalendar(@RequestBody UserCalendarDTO userCalendarDTO) {
        return userService.updateUserCalendar(userCalendarDTO);
    }


    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody User user) {
        //return message or mail code
        if (userService.isExist(user.getEmail())) {
            return ResponseEntity.status(204).body("isExist");
        } else {
            return ResponseEntity.ok(userService.emailAuth(user.getEmail()));
        }
    }


    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUserC(user));
    }

    @PostMapping("/login")
    @ResponseBody
    public Object login(@RequestBody User user) {
        //return message or find user
        //email,pass
        User user1 = userService
                .getUser(user.getEmail(), user.getPassword());

        if (user1 != null && userService.isExist(user.getEmail())) {
            UserCalendar userCalendar = user1.getUserCalendar();
            userService.checkLongLogin(user1);

            UserCalendarDTO userCalendarDTO = userCalendar.toDTO();

            return ResponseEntity.ok(userCalendarDTO);
        } else {
            return ResponseEntity.status(404).body("user");
        }
    }

    //<<<pass+email
    //email-code>>>front
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody User user) {
        if (userService.isExist(user.getEmail())) {
            return ResponseEntity.ok(userService.emailAuth(user.getEmail()));
        } else {
            return ResponseEntity.status(404).body("User doesn't exist");
        }
    }

    //saved changed pass
    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestBody User user) {
        return ResponseEntity.ok(userService.savePass(user));
    }

    //200ok 400bad
    @PostMapping("/changeName")
    public ResponseEntity<String> changeName(@RequestBody User user) {
        if (userService.changeName(user.getId(), user.getUsername())) {
            return ResponseEntity.ok().body("ok");
        } else {
            return ResponseEntity.status(400).body("bad");
        }
    }

    @PostMapping("/deleteAccount")
    public ResponseEntity<String> deleteAccount(@RequestBody User user) {
        if (userService.delUser(user.getId())) {
            return ResponseEntity.ok().body("User deleted");
        } else return ResponseEntity.status(400).body("bad");
    }

    @PostMapping("/contact")
    public ResponseEntity<String> contactUs(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String question = requestBody.get("question");
        if (email != null && question != null && userService.contactUs(email, question)) {
            return ResponseEntity.ok().body("");
        } else return ResponseEntity.status(400).body("");
    }


}
