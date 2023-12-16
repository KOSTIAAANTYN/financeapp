package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody User user) {
        //return bool or code
        if (userService.isExist(user)) {
            return "isExsist";
        } else {
            return userService.emailAuth(user);
        }
    }

    @PostMapping("/createUser")
    public String createUser(@RequestBody User user) {
        userService.saveUser(user);
        System.out.println(user);
        return "All good";
    }

    @PostMapping("/login")
    @ResponseBody
    public Object login(@RequestBody  User user) {
        //return message or find user
        if (userService.isExist(user)) {
            return userService.getUser(user);
        } else {
            return "User isn't exist";
        }
    }

}
