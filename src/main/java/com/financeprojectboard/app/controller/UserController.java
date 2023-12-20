package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    private User userHere;


    @PostMapping("/sendEmail")
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


}
