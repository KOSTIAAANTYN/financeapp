package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    private User userHere;//TODO hide this



    @PostMapping("/sendEmail")//TODO mod this
    public ResponseEntity<String> sendEmail(@RequestBody User user) {
        //return message or mail code
        if (userService.isExist(user)) {
            return ResponseEntity.status(204).body("isExist");
        } else {
            userHere = user;
            return ResponseEntity.ok(userService.emailAuth(user));
        }
    }


    @PostMapping("/createUser")
    public ResponseEntity<String> createUser() {
        //take date from prev req
        //saved user
        return ResponseEntity.ok(userService.saveUser(userHere));
    }

    @PostMapping("/login")
    @ResponseBody
    public Object login(@RequestBody User user) {
        //return message or find user
        if (userService.isExist(user)) {
            return userService.getUser(user);
        } else {
            return ResponseEntity.status(404).body("user");
        }
    }

    //<<<pass+email
    //email-code>>>front
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody User user) {
        if (userService.isExist(user)) {
            userHere = user;
            return ResponseEntity.ok(userService.emailAuth(user));
        } else {
            return ResponseEntity.status(404).body("User doesn't exist");
        }

    }

    //saved changed pass
    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword() {
        return ResponseEntity.ok(userService.savePass(userHere));
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
