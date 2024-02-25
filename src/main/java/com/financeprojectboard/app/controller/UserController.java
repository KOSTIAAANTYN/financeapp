package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.DTO.MessageDTO;
import com.financeprojectboard.app.DTO.MessageRequestDTO;
import com.financeprojectboard.app.DTO.UserDTO;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    //TODO sync by front (I deleted userHere and no req without user)
    //TODO code saves always false

    //test new
    @PostMapping("/testUser")
    public ResponseEntity<String> testUser(@RequestBody User user) {

        return ResponseEntity.ok(userService.saveUserC(user));
    }


    @PostMapping("/saveMessages")
    public ResponseEntity<String> saveMessage(@RequestBody MessageRequestDTO messageRequestDTO) {
        Long userId = messageRequestDTO.getUserId();
        Long dayId = messageRequestDTO.getDayId();
        List<MessageDTO> messages = messageRequestDTO.getMessages();
        userService.saveMessage(userId, dayId, messages);
        return ResponseEntity.ok("Messages saved successfully");
    }




    @PostMapping("/getUser")
    @ResponseBody
    public ResponseEntity<UserDTO> getUser(@RequestBody User user1) {
        User user = userService.getUser(user1.getId());
        if (user != null) {
            UserDTO userDTO = user.toDTO();
            return ResponseEntity.ok(userDTO);
        } else return ResponseEntity.badRequest().build();
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
        if (userService.isExist(user.getEmail())) {
            userService.checkLongLogin(user);
            return userService.getUser(user);
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
