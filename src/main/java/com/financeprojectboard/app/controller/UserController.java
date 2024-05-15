package com.financeprojectboard.app.controller;

import com.financeprojectboard.app.DTO.UserCalendarDTO;
import com.financeprojectboard.app.DTO.UserHistoryDTO;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserCalendar;
import com.financeprojectboard.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Tag(name = "main_methods")
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    //http://localhost:8080/swagger-ui/index.html#/

    @Operation(
            summary = "receive user and update his calendar",
            description = "if no id + message , id + message+changes=update, id_base not found = delete"
    )
    @PostMapping("/updateCalendar")
    public ResponseEntity<UserCalendarDTO> updateCalendar(@RequestBody UserCalendarDTO userCalendarDTO) {
        return  ResponseEntity.ok(userService.updateCalendar(userCalendarDTO));
    }

    @Operation(
            summary = "receive user history and add him",
            description = "add to history or throw err User not found with id:"
    )
    @PostMapping("/addToHistory")
    public ResponseEntity<String> addToHistory(@RequestBody UserHistoryDTO userHistoryDTO) {
        userService.addToHistory(userHistoryDTO);
        return ResponseEntity.ok().body("ok");
    }

    @Operation(
            summary = "receive user id and index of history in list, then delete him",
            description = "remove history by user id and index or throw err User not found with id:"
    )
    @PostMapping("/removeOneHistoryElem")
    public ResponseEntity<String> removeOneHistoryElem(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId");
        Long index = requestBody.get("index");
        userService.removeOneHistoryElem(userId, index);
        return ResponseEntity.ok().body("ok");
    }

    @Operation(
            summary = "receive user id and index of history in list, then delete him",
            description = "remove history by user id and index or throw err User not found with id:"
    )
    @PostMapping("/clearHistory")
    public ResponseEntity<String> clearHistory(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId");
        userService.clearHistory(userId);
        return ResponseEntity.ok().body("ok");
    }



    @Operation(
            summary = "receive user email, send code to front"
    )
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody User user) {
        //return message or mail code
        if (userService.isExist(user.getEmail())) {
            return ResponseEntity.status(204).body("isExist");
        } else {
            return ResponseEntity.ok(userService.emailAuth(user.getEmail()));
        }
    }

    @Operation(
            summary = "receive user and save him"
    )
    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUserC(user));
    }

    @Operation(
            summary = "auth user and update old calendar"
    )
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

    @Operation(
            summary = "<<<pass+email/email-code>>>front"
    )
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody User user) {
        if (userService.isExist(user.getEmail())) {
            return ResponseEntity.ok(userService.emailAuth(user.getEmail()));
        } else {
            return ResponseEntity.status(404).body("User doesn't exist");
        }
    }

    @Operation(
            summary = "saved changed pass"
    )
    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestBody User user) {
        return ResponseEntity.ok(userService.savePass(user));
    }

    @Operation(
            summary = "change name",
            description = "200ok 400bad"
    )
    @PostMapping("/changeName")
    public ResponseEntity<String> changeName(@RequestBody User user) {
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
    public ResponseEntity<String> deleteAccount(@RequestBody User user) {
        if (userService.delUser(user.getId())) {
            return ResponseEntity.ok().body("User deleted");
        } else return ResponseEntity.status(400).body("bad");
    }

    @Operation(
            summary = "contact with devs by email"
    )
    @PostMapping("/contact")
    public ResponseEntity<String> contactUs(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String question = requestBody.get("question");
        if (email != null && question != null && userService.contactUs(email, question)) {
            return ResponseEntity.ok().body("");
        } else return ResponseEntity.status(400).body("");
    }


}
