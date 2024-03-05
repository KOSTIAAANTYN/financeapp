package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.DTO.UserCalendarDTO;
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

    @PostMapping("/test")
    public Object test(@RequestBody UserCalendarDTO userCalendarDTO) {
        userService.test(userCalendarDTO);
        return ResponseEntity.ok();
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
    //TODO
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
