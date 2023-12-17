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
    //<<пароль+email
    //email-code(front)
    //сохран изм пароля

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody User user) {
        //return bool or code
        if (userService.isExist(user)) {
            return "isExist";
        } else {
            return userService.emailAuth(user);
        }
    }

    //берет дание с пред запраса
    @PostMapping("/createUser")
    public String createUser(@RequestBody User user) {
        //save user
        userService.saveUser(user);
        return "All good";
    }

    @PostMapping("/login")
    @ResponseBody
    public Object login(@RequestBody  User user) {
        //return message or find user
        if (userService.isExist(user)) {
            return userService.getUser(user);
        } else if (user.getPassword().equals("")){
            return "false";
        }else {
            return "false";
        }
    }

}
