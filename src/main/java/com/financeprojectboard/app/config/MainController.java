package com.financeprojectboard.app.config;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/secured")
public class MainController {
    @GetMapping("/user")
    public Object userAccess(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return null;
        }

        return userDetails.getUsername()+" "+userDetails.getUserCalendarId();
    }

}
