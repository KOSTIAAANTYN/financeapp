package com.financeprojectboard.app.service;

import com.financeprojectboard.app.DTO.MessageDTO;
import com.financeprojectboard.app.model.CalendarDay;
import com.financeprojectboard.app.model.Message;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserCalendar;
import com.financeprojectboard.app.repositories.CalendarDayRepository;
import com.financeprojectboard.app.repositories.MessageRepository;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CalendarDayRepository calendarDayRepository;
    private final MessageRepository messageRepository;
    private final JavaMailSender mailSender;


    public void saveMessage(Long userId, Long dayId, List<MessageDTO> messages) {
        if (userRepository.findById(userId).isPresent()) {
            User user = userRepository.findById(userId).get();
            CalendarDay day = user.getUserCalendar().getCalendar().get(dayId.intValue());

            for (int i = 0; i < messages.size(); i++) {
                if (day.getMessages().size() <= i) {
                    // Add new
                    MessageDTO messageDTO = messages.get(i);
                    Message message = new Message(
                            messageDTO.getIsIncome(), messageDTO.getDescription(), messageDTO.getPrice());
                    message.setCalendarDay(day);
                    messageRepository.save(message);
                } else {
                    // Update
                    MessageDTO messageDTO = messages.get(i);
                    Message message = day.getMessages().get(i);

                    if (!messageDTO.equals(message.toDTO())) {
                        message.setIncome(messageDTO.getIsIncome());
                        message.setDescription(messageDTO.getDescription());
                        message.setPrice(messageDTO.getPrice());
                        messageRepository.save(message);
                    }
                }
            }
            calendarDayRepository.save(day);
        }
    }


    //test new
    public String saveUserC(User user) {
        User user1 = new User(user.getUsername(), user.getEmail(), user.getPassword());
        userRepository.save(user1);
        return "ok";
    }


    public void checkLongLogin(User user) {
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        User existingUser = userRepository.findByEmail(user.getEmail());
        UserCalendar userCalendar = existingUser.getUserCalendar();
        List<CalendarDay> calendar = userCalendar.getCalendar();

        if (!calendar.isEmpty()) {
            CalendarDay lastCalendarDay = calendar.get(calendar.size() - 1);
            LocalDate lastLoginDay = LocalDate.parse(lastCalendarDay.getFullDate(), dtf);
            LocalDate localDate = LocalDate.now();

            Period period = Period.between(lastLoginDay, localDate);
            int daysBetween = period.getDays();

            if (daysBetween > 35) {
                user.generate0Calendar();
            } else {
                for (int i = 1; i <= daysBetween; i++) {
                    LocalDate nextDay = lastLoginDay.plusDays(i);
                    userCalendar.addDay(nextDay.format(dt), nextDay.format(dtf));
                }
            }
        }

    }


    public String savePass(User user) {
        if (isExist(user.getEmail())) {
            User oldUser = userRepository.findByEmail(user.getEmail());
            oldUser.setPassword(user.getPassword());
            userRepository.save(oldUser);
            return "Password saved";
        } else {
            return "User doesn't exist";
        }
    }


    public boolean isExist(String email) {
        return userRepository.findByEmail(email) != null;
    }


    public String emailAuth(String email) {
        String code = generateRandomCode();
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom("financeprojectboard@gmail.com");
            helper.setTo(email);
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

    //get by email and equals pass
    public User getUser(String email,String password) {
        if (password.equals(userRepository.findByEmail(email).getPassword())) {
            return userRepository.findByEmail(email);
        }else return null;
    }

    public boolean changeName(Long id, String username) {
        if (userRepository.findById(id).isPresent()) {
            User user = userRepository.findById(id).get();
            user.setUsername(username);
            userRepository.save(user);
            return true;
        } else return false;
    }

    public boolean delUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.delete(userRepository.findById(id).get());
            return true;
        } else return false;
    }

    public boolean contactUs(String email, String question) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("financeprojectboard@gmail.com");
            helper.setTo("ichkostya@gmail.com");//another Dev (maybe)
            helper.setText(question);
            helper.setSubject(email);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
        return true;
    }


}
