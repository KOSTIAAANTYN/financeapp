package com.financeprojectboard.app.service;

import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;


    public void saveUser(User user) {
        userRepository.save(user);
    }


    public boolean isExist(User user) {
        return userRepository.findByEmail(user.getEmail()) != null;
    }


    public String emailAuth(User user) {
        String code = generateRandomCode();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("financeprojectboard@gmail.com");
        message.setTo(user.getEmail());
        message.setText(code);
        message.setSubject("Your code for finance app");

        mailSender.send(message);
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
        if(user.getPassword().equals(userRepository.findByEmail(user.getEmail()).getPassword())){
            return userRepository.findByEmail(user.getEmail());
        }else {
            return "false";
        }

    }
}
