package com.financeprojectboard.app.service;

import com.financeprojectboard.app.DTO.CalendarDayDTO;
import com.financeprojectboard.app.DTO.MessageDTO;
import com.financeprojectboard.app.DTO.UserCalendarDTO;
import com.financeprojectboard.app.DTO.UserHistoryDTO;
import com.financeprojectboard.app.model.*;
import com.financeprojectboard.app.repositories.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserCalendarRepository userCalendarRepository;
    private final CalendarDayRepository calendarDayRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final JavaMailSender mailSender;
    private final MessageRepository messageRepository;


    @Transactional
    public UserCalendarDTO updateCalendar(UserCalendarDTO userCalendarDTO) {
        User user = userRepository.findById(userCalendarDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userCalendarDTO.getId()));

        UserCalendar existingUserCalendar = user.getUserCalendar();

        for (CalendarDayDTO calendarDayDTO : userCalendarDTO.getCalendar()) {
            CalendarDay existingDay = existingUserCalendar.getCalendar().stream()
                    .filter(calendarDay -> calendarDay.getId().equals(calendarDayDTO.getId()))
                    .findFirst().orElse(null);

            if (existingDay != null) {
                // Delete messages
                List<Message> messagesToDelete = existingDay.getMessages().stream()
                        .filter(message -> calendarDayDTO.getMessages().stream()
                                .noneMatch(messageDTO -> messageDTO.getId() != null
                                        && messageDTO.getId().equals(message.getId())))
                        .collect(Collectors.toList());

                existingDay.getMessages().removeAll(messagesToDelete);
                messageRepository.deleteAll(messagesToDelete);

                for (MessageDTO messageDTO : calendarDayDTO.getMessages()) {
                    if (messageDTO.getId() != null) {
                        Message existingMessage = existingDay.getMessages().stream()
                                .filter(message -> message.getId().equals(messageDTO.getId()))
                                .findFirst().orElse(null);

                        if (existingMessage != null) {
                            // Update message
                            if (!existingMessage.getDescription().equals(messageDTO.getDescription())
                                    || existingMessage.getPrice() != messageDTO.getPrice()
                                    || existingMessage.isIncome() != messageDTO.getIsIncome()) {
                                existingMessage.setDescription(messageDTO.getDescription());
                                existingMessage.setPrice(messageDTO.getPrice());
                                existingMessage.setIncome(messageDTO.getIsIncome());
                            }
                        } else {
                            throw new EntityNotFoundException("Message not found with id: " + messageDTO.getId());
                        }
                    } else {
                        // Create message
                        Message newMessage = messageDTO.toEntity(existingDay);
                        existingDay.getMessages().add(newMessage);
                    }
                }
            } else {
                CalendarDay newCalendarDay = calendarDayDTO.toEntity(existingUserCalendar);
                existingUserCalendar.getCalendar().add(newCalendarDay);
            }
        }
        existingUserCalendar.allTotal();
        userCalendarRepository.save(existingUserCalendar);
        return existingUserCalendar.toDTO();
    }


    public void addToHistory(UserHistoryDTO userHistoryDTO) {
        User user = userRepository.findById(userHistoryDTO.getId()).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userHistoryDTO.getId()));
        user.getUserHistory().add(userHistoryDTO.toEntity(user));
        userRepository.save(user);

    }

    @Transactional
    public void removeOneHistoryElem(Long userId, Long index) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userId));
        Long id = user.getUserHistory().get(index.intValue()).getId();

        user.getUserHistory().remove(index.intValue());
        userHistoryRepository.deleteById(id);
    }


    @Transactional
    public void clearHistory(Long userId) {
        userHistoryRepository.deleteUserHistoriesByUserId(userId);
    }


    public String saveUserC(User user) {
        User user1 = new User(user.getUsername(), user.getEmail(), user.getPassword());
        userRepository.save(user1);
        return "ok";
    }

    public void checkLongLogin(User user) {

        DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");


        UserCalendar userCalendar = user.getUserCalendar();
        List<CalendarDay> calendar = userCalendar.getCalendar();


        if (!calendar.isEmpty()) {
            CalendarDay lastCalendarDay = calendar.get(calendar.size() - 1);
            LocalDate lastLoginDay = LocalDate.parse(lastCalendarDay.getFullDate(), dtf);
            LocalDate localDate = LocalDate.now();

            Period period = Period.between(lastLoginDay, localDate);
            int daysBetween = period.getDays() + period.getMonths() * 30 + period.getYears() * 365;

            if (daysBetween >= 35) {
                //dell old and gen new -1d
                user.deleteUserCalendar();
                userCalendarRepository.delete(userCalendar);
                userRepository.save(user);

                user.generate0Calendar();
                userRepository.save(user);
            } else {
                for (int i = 1; i <= daysBetween; i++) {
                    //add new del first
                    LocalDate nextDay = lastLoginDay.plusDays(i);
                    userCalendar.addDay(nextDay.format(dt), nextDay.format(dtf));

                    Long id = calendar.get(0).getId();

                    calendar.remove(0);
                    calendarDayRepository.deleteById(id);

                }
            }
            userCalendarRepository.save(userCalendar);
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
    public User getUser(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && password.equals(user.getPassword())) {
            return userRepository.findByEmail(email);
        } else return null;
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
