package com.financeprojectboard.app.service;

import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;


    public String saveUser(User user) {
        if (isExist(user)){
            return "User exist";
        }else{
            userRepository.save(user);
            return "User saved";
        }
    }

    public String savePass(User user) {
        if (isExist(user)){
            User oldUser = userRepository.findByEmail(user.getEmail());
            oldUser.setPassword(user.getPassword());
            userRepository.save(oldUser);
            return "Password saved";
        }else {
            return "User doesn't exist";
        }

    }


    public boolean isExist(User user) {
        return userRepository.findByEmail(user.getEmail()) != null;
    }


    public String emailAuth(User user) {
        String code = generateRandomCode();
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom("financeprojectboard@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Your code for finance app");


            ClassPathResource resource = new ClassPathResource("templates/codeMail.html");
            byte[] fileData = resource.getInputStream().readAllBytes();
            String htmlContent = new String(fileData, StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("<!-- GENERATED_CODE_PLACEHOLDER -->", code);

            helper.setText(htmlContent, true);

            mailSender.send(message);


        } catch (MessagingException | IOException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return code;
    }



    public static String generateRandomCode() {
        int length = 6;

        String symbols = "0123456789";

        Random random = new Random();

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(symbols.length());

            code.append(symbols.charAt(index));
        }
        return code.toString();
    }


    public Object getUser(User user) {
        if (user.getPassword().equals(userRepository.findByEmail(user.getEmail()).getPassword())) {
            return ResponseEntity.ok(userRepository.findByEmail(user.getEmail()));
        } else {
            return ResponseEntity.status(404).body("user");
        }

    }

}
